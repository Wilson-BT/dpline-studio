package com.dpline.k8s.operator.service;

import com.dpline.common.util.Asserts;
import com.dpline.common.util.CollectionUtils;
import com.dpline.dao.mapper.FlinkSessionMapper;
import com.dpline.k8s.operator.entry.IngressV1CustomConfig;
import com.dpline.k8s.operator.entry.IngressV1RulePath;
import com.dpline.k8s.operator.job.ClusterFlushEntity;
import com.dpline.k8s.operator.k8s.K8sClusterManager;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.api.model.networking.v1.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class TaskIngressV1Service extends BaseService implements IngressService {

    private static final Logger logger = LoggerFactory.getLogger(TaskIngressV1Service.class);

    @Autowired
    FlinkSessionMapper flinkSessionMapper;

    @Autowired
    K8sClusterManager k8sClusterManager;

    @Autowired
    TaskClusterMapService taskClusterMapService;

    private static final String K8S_INGRESS_V1_VERSION = "networking.k8s.io/v1";

    private static final String NGINX_REWRITE_V1 = "nginx.ingress.kubernetes.io/rewrite-target";

    private static final String DEFAULT_NGINX_VALUE = "/$2";

    /**
     * 用于更新 ingress，初始化的时候需要根据 caffeineCache 初始化一遍
     * key: kubePath + nameSpace
     * value:
     * key:   clusterId
     * value: HTTPIngressPath
     * <p>
     * k8s application delete every time
     * 只有在发生delete session的时候才删除 k8s session 的 cluster 信息，防止误删掉了其他正在运行的应用
     */
    @Autowired
    public void setK8sClusterManager(K8sClusterManager k8sClusterManager) {
        this.k8sClusterManager = k8sClusterManager;
    }

    /**
     * 创建ingress
     * 在创建session的时候调用
     * 或者创建任务的时候调用
     *
     */
    public synchronized void addIngressRule(ClusterFlushEntity clusterFlushEntity) {
        addIngressRuleAndAnnotations(clusterFlushEntity);
        logger.info("[V1] ClusterId [{}] ingressPath had been added into k8s ingress", clusterFlushEntity.getClusterId());
    }

    /**
     * session 模式删除 session 集群
     * application 模式 清理每个 app 的时候
     *
     */
    public synchronized boolean clearIngressRule(ClusterFlushEntity clusterFlushEntity) {
        return  deleteIngressRuleAndAnnotations(clusterFlushEntity);
    }

    /**
     * 初次创建ingress,需要遍历nameSpace下所有的规则
     *
     * @return
     */
    public synchronized boolean createIngress(Long clusterEntityId, Map<String, String> nginxRewriteMap, KubernetesClient k8sClient) {
        ArrayList<HTTPIngressPath> httpIngressPathList = new ArrayList<>();
        Optional<ConcurrentHashMap<String, ClusterFlushEntity>> clusterIdEntityMap = taskClusterMapService.getClusterIdEntityMap(clusterEntityId);
        if(!clusterIdEntityMap.isPresent()){
            return false;
        }
        Ingress ingress = null;
        try {
            AtomicReference<String> ingressHost = new AtomicReference<>("");
            AtomicReference<String> ingressName = new AtomicReference<>("");
            clusterIdEntityMap.get().forEach((key, value) -> {
                IngressV1RulePath ingressV1RulePath = new IngressV1RulePath(
                    value.getClusterId(),
                    value.getNameSpace());
                httpIngressPathList.add(ingressV1RulePath.getHttpIngressPath());
                ingressHost.set(value.getIngressHost());
                ingressName.set(value.getIngressName());
            });
            ArrayList<IngressRule> ingressRules = new ArrayList<>();
            ingressRules.add(
                new IngressRule(ingressHost.get(),
                    new HTTPIngressRuleValueBuilder()
                        .withPaths(httpIngressPathList)
                        .build()));
            ingress =
                new IngressBuilder()
                    .withApiVersion(K8S_INGRESS_V1_VERSION)
                    .withNewMetadata()
                    .withName(ingressName.get())
                    .withAnnotations(nginxRewriteMap)
                    .endMetadata()
                    .withNewSpec()
                    .withRules(ingressRules)
                    .endSpec()
                    .build();
            k8sClient.network().v1().ingresses().createOrReplace(ingress);
            return true;
        } catch (Exception e) {
            logger.error("Create ingress template failed. Ingress => [{}]", ingress);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 新增 ingress 的rule，并添加注解标记
     *
     * @return
     */
    private synchronized boolean addIngressRuleAndAnnotations(ClusterFlushEntity clusterFlushEntity) {
        Long clusterEntityId = clusterFlushEntity.getClusterEntityId();
        Optional<KubernetesClient> k8sClientInstance = k8sClusterManager.getK8sClientInstance(clusterEntityId);
        // 首先获取到有没有相应的ingress，如果有的话，解析到，然后在后面追加
        // 如果没有获取到，那么就直接创建
        // 此处不需要过滤掉指定的clusterId，即需要全部创建
        IngressV1CustomConfig ingressV1CustomConfig = createIngressCustomConfig(clusterEntityId);
        return k8sClientInstance.map(k8sClient -> {
            // 如果存在deployment，那么就更新ingress
            boolean deploymentExists = k8sClusterManager.isDeploymentExists(
                clusterFlushEntity.getClusterId(),
                clusterFlushEntity.getClusterEntityId());
            if (deploymentExists) {
                try {
                    createOrReplaceIngress(k8sClient, clusterFlushEntity.getNameSpace(),ingressV1CustomConfig);
                    return true;
                } catch (Exception e) {
                    logger.warn("[V1] Add ingress rule error.\n",e);
                }
            } else {
                logger.warn("[V1] While create cluster ingress, Could not find deployment {}", clusterFlushEntity.getClusterId());
            }
            return false;
        }).orElse(false);
    }

    private void createOrReplaceIngress(KubernetesClient k8sClient,String nameSpace, IngressV1CustomConfig ingressCustomConfig) {
        // 查询是否存在，存在就更新
        Ingress ingress = k8sClient.network()
            .v1()
            .ingresses()
            .inNamespace(nameSpace)
            .withName(ingressCustomConfig.getIngressName())
            .get();
        if (Asserts.isNotNull(ingress)){
            List<IngressRule> rules = ingress.getSpec().getRules();
            Optional<IngressRule> firstIngressRule = rules.stream().findFirst();
            firstIngressRule.ifPresent(
                ingressRule ->
                    ingressRule.getHttp()
                    .setPaths(ingressCustomConfig.getHttpIngressPathList()));
        } else {
            // 不存在就新建
            ArrayList<IngressRule> ingressRules = new ArrayList<>();
            ingressRules.add(
                new IngressRule(ingressCustomConfig.getIngressHost(),
                    new HTTPIngressRuleValueBuilder()
                        .withPaths(ingressCustomConfig.getHttpIngressPathList())
                        .build()));
            ingress =
                new IngressBuilder()
                    .withApiVersion(K8S_INGRESS_V1_VERSION)
                    .withNewMetadata()
                    .withName(ingressCustomConfig.getIngressName())
                    .withAnnotations(ingressCustomConfig.getNginxRewriteMap())
                    .endMetadata()
                    .withNewSpec()
                    .withIngressClassName("nginx")
                    .withRules(ingressRules)
                    .endSpec()
                    .build();
        }
        k8sClient.network().v1().ingresses().createOrReplace(ingress);
    }

    /**
     * 替换，或者直接全部删除ingress
     *
     * @param k8sClient
     * @param ingressCustomConfig
     */
    public void replaceOrDeleteIngress(KubernetesClient k8sClient, String nameSpace, IngressV1CustomConfig ingressCustomConfig) {
        // 查询是否存在，存在就更新
        Ingress ingress = k8sClient.network()
            .v1()
            .ingresses()
            .withName(ingressCustomConfig.getIngressName())
            .get();
        if (Asserts.isNull(ingress)){
            logger.warn("[V1] Ingress with name => {} is not exists.", ingressCustomConfig.getIngressName());
            return;
        }
        // 如果剩余任务为空，直接删除ingress
        if(CollectionUtils.isEmpty(ingressCustomConfig.getHttpIngressPathList())){
            logger.info("[V1] Task is empty, prepare to delete the whole ingress.");
            List<StatusDetails> delete = k8sClient.network()
                    .v1()
                    .ingresses()
                    .inNamespace(nameSpace)
                    .withName(ingressCustomConfig.getIngressName())
                    .delete();
            logger.info("[V1] Whole delete result: {}", delete.toString());
            return;
        }
        // 剩余任务不为空，直接替换
        logger.info("[V1] Task is not empty, prepare to replace the ingress.");
        ingress.getMetadata().setAnnotations(ingressCustomConfig.getNginxRewriteMap());
        Optional<IngressRule> firstOpt = ingress.getSpec().getRules().stream().findFirst();
        firstOpt.ifPresent(firstIngressRule->{
            firstIngressRule
                .getHttp()
                .setPaths(ingressCustomConfig.getHttpIngressPathList());
        });
        k8sClient.network()
            .v1()
            .ingresses()
            .replace(ingress);
        logger.info("[V1] Ingress replace success.");
        return;
    }

    public IngressV1CustomConfig createIngressCustomConfig(Long clusterEntityId) {
        IngressV1CustomConfig ingressCustomConfig = new IngressV1CustomConfig();
        List<HTTPIngressPath> httpIngressPathsList = new ArrayList<>();
        //  写入标记
        HashMap<String, String> nginxRewriteMap = new HashMap<>();
        nginxRewriteMap.put(NGINX_REWRITE_V1, DEFAULT_NGINX_VALUE);
        ingressCustomConfig.setNginxRewriteMap(nginxRewriteMap);
        Optional<ConcurrentHashMap<String, ClusterFlushEntity>> clusterIdEntityMapOptional = taskClusterMapService.getClusterIdEntityMap(clusterEntityId);

        clusterIdEntityMapOptional.ifPresent(clusterIdEntityMap ->
            {
                AtomicReference<String> ingressHost = new AtomicReference<>("");
                AtomicReference<String> ingressName = new AtomicReference<>("");
                clusterIdEntityMap.forEach((key, value) -> {
                    IngressV1RulePath ingressV1RulePath = new IngressV1RulePath(value.getClusterId(), value.getNameSpace());
                    httpIngressPathsList.add(ingressV1RulePath.getHttpIngressPath());
                    ingressHost.set(value.getIngressHost());
                    ingressName.set(value.getIngressName());
                });

                ingressCustomConfig.setHttpIngressPathList(httpIngressPathsList);
                ingressCustomConfig.setIngressHost(ingressHost.get());
                ingressCustomConfig.setIngressName(ingressName.get());
            }
        );
        return ingressCustomConfig;
    }


    /**
     * 删除ingress，如果 deployment 存在，需要同时删除掉deployment
     *
     * @return
     */
    private synchronized boolean deleteIngressRuleAndAnnotations(ClusterFlushEntity clusterFlushEntity) {
        // 此处需要过滤掉指定的clusterId，只需要单独删除，如果删除失败
        Long clusterEntityId = clusterFlushEntity.getClusterEntityId();
        IngressV1CustomConfig ingressCustomConfig = createIngressCustomConfig(clusterEntityId);
        Optional<KubernetesClient> k8sClientInstance = k8sClusterManager.getK8sClientInstance(clusterEntityId);
        return k8sClientInstance.map(k8sClient -> {
            boolean deploymentExists = k8sClusterManager.isDeploymentExists(
                clusterFlushEntity.getClusterId(),
                clusterFlushEntity.getClusterEntityId());
            // 如果有应用还存在，或者还是 running 那么Pod应该还是running
            if (deploymentExists) {
                logger.warn("[V1] Find deployment [{}] is still be alive,", clusterFlushEntity.getClusterId());
                k8sClusterManager.deleteDeploymentByClient(k8sClient,clusterFlushEntity.getClusterId());
                logger.warn("[V1] Deployment on clusterId [{}]  has been be deleted.", clusterFlushEntity.getClusterId());
                k8sClusterManager.deleteConfigMapsByClient(k8sClient,clusterFlushEntity.getClusterId());
            }
            try {
                replaceOrDeleteIngress(k8sClient, clusterFlushEntity.getNameSpace(),ingressCustomConfig);
                return true;
            } catch (Exception e) {
                logger.error("Delete ingress Rule Failed.{}", e.toString());
                return false;
            }
        }).orElse(false);
    }

}
