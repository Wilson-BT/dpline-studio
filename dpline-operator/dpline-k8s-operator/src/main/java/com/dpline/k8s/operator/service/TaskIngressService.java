package com.dpline.k8s.operator.service;

import com.dpline.common.util.Asserts;
import com.dpline.common.util.CollectionUtils;
import com.dpline.dao.mapper.FlinkSessionMapper;
import com.dpline.k8s.operator.entry.IngressCustomConfig;
import com.dpline.k8s.operator.entry.IngressRulePath;
import com.dpline.k8s.operator.job.ClusterFlushEntity;
import com.dpline.k8s.operator.k8s.K8sClusterManager;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.networking.v1beta1.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class TaskIngressService extends BaseService implements IngressService {

    private static final Logger logger = LoggerFactory.getLogger(TaskIngressService.class);

    @Autowired
    FlinkSessionMapper flinkSessionMapper;

    @Autowired
    K8sClusterManager k8sClusterManager;

    @Autowired
    TaskClusterMapService taskClusterMapService;

    public static final String K8S_INGRESS_VERSION = "networking.k8s.io/v1beta1";

    public static final String NGINX_REWRITE = "nginx.org/rewrites";


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
        addIngressRuleAndAnnotations(clusterFlushEntity.getClusterEntityId(),
                                     clusterFlushEntity.getClusterId());
        logger.info("ClusterId [{}] ingressPath had been added into k8s ingress", clusterFlushEntity.getClusterId());
    }

    /**
     * 只在删除 session 的时候外部调用
     *
     */
    public synchronized boolean clearIngressRule(ClusterFlushEntity clusterFlushEntity) {
        return deleteIngressRuleAndAnnotations(clusterFlushEntity);
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
            // host name 要从 cluster 中找
            AtomicReference<String> ingressHost = new AtomicReference<>("");
            AtomicReference<String> ingressName = new AtomicReference<>("");
            clusterIdEntityMap.get().forEach((key, value) -> {
                IngressRulePath ingressRulePath = new IngressRulePath(value.getNameSpace(),value.getClusterId());
                httpIngressPathList.add(ingressRulePath.getHttpIngressPath());
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
                    .withApiVersion(K8S_INGRESS_VERSION)
                    .withNewMetadata()
                    .withName(ingressName.get())
                    .withAnnotations(nginxRewriteMap)
                    .endMetadata()
                    .withNewSpec()
                    .withRules(ingressRules)
                    .endSpec()
                    .build();
            k8sClient.network().ingresses().createOrReplace(ingress);
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
     * @param clusterEntityId
     * @param clusterId
     * @return
     */
    private synchronized boolean addIngressRuleAndAnnotations(Long clusterEntityId,
                                                              String clusterId) {
        Optional<KubernetesClient> k8sClientInstance = k8sClusterManager.getK8sClientInstance(clusterEntityId);
        // 首先获取到有没有相应的ingress，如果有的话，解析到，然后在后面追加
        // 如果没有获取到，那么就直接创建
        // 此处不需要过滤掉指定的clusterId，即需要全部创建
        IngressCustomConfig ingressCustomConfig = createIngressCustomConfig(clusterEntityId);
        return k8sClientInstance.map(k8sClient -> {
            final Deployment deployment =
                k8sClient
                    .apps()
                    .deployments()
                    .withName(clusterId)
                    .get();
            if (deployment == null) {
                logger.warn("While create cluster ingress, Could not find deployment {}", clusterId);
                return false;
            }
            try {
                createOrReplaceIngress(k8sClient, ingressCustomConfig);
                return true;
            } catch (Exception e) {
                logger.warn("Add ingress rule error.\n",e);
            }
            return false;
        }).orElse(false);
    }

    private void createOrReplaceIngress(KubernetesClient k8sClient, IngressCustomConfig ingressCustomConfig) {
        // 查询是否存在，存在就更新
        Ingress ingress = k8sClient.network()
            .ingress()
            .withName(ingressCustomConfig.getIngressName())
            .get();
        if (Asserts.isNotNull(ingress)){
            ingress.getMetadata().setAnnotations(ingressCustomConfig.getNginxRewriteMap());
            List<IngressRule> rules = ingress.getSpec().getRules();
            IngressRule ingressRule = rules.get(0);
            ingressRule.getHttp().setPaths(ingressCustomConfig.getHttpIngressPathList());
            k8sClient.network().ingress().replace(ingress);
            return;
        }
        // 不存在就新建
        ArrayList<IngressRule> ingressRules = new ArrayList<>();
        ingressRules.add(
            new IngressRule(ingressCustomConfig.getIngressHost(),
                new HTTPIngressRuleValueBuilder()
                    .withPaths(ingressCustomConfig.getHttpIngressPathList())
                    .build()));
        ingress =
            new IngressBuilder()
                .withApiVersion(K8S_INGRESS_VERSION)
                .withNewMetadata()
                .withName(ingressCustomConfig.getIngressName())
                .withAnnotations(ingressCustomConfig.getNginxRewriteMap())
                .endMetadata()
                .withNewSpec()
                .withRules(ingressRules)
                .endSpec()
                .build();
        k8sClient.network().ingresses().create(ingress);
    }

    /**
     * 替换，或者直接全部删除ingress
     *
     * @param k8sClient
     * @param ingressCustomConfig
     */
    public void replaceOrDeleteIngress(KubernetesClient k8sClient, IngressCustomConfig ingressCustomConfig) {
        // 查询是否存在，存在就更新
        Ingress ingress = k8sClient.network()
            .ingress()
            .withName(ingressCustomConfig.getIngressName())
            .get();
        if (Asserts.isNull(ingress)){
            return;
        }
        // 如果为空，直接删除
        if(CollectionUtils.isEmpty(ingressCustomConfig.getHttpIngressPathList())){
            logger.info("Task is empty, prepare to delete the whole ingress.");
            k8sClient.network().ingress().withName(ingressCustomConfig.getIngressName()).delete();
            return;
        }
        // 不为空，直接替换
        logger.info("Task is not empty, prepare to replace the ingress.");
        ingress.getMetadata().setAnnotations(ingressCustomConfig.getNginxRewriteMap());
        List<IngressRule> rules = ingress.getSpec().getRules();
        IngressRule ingressRule = rules.get(0);
        ingressRule.getHttp().setPaths(ingressCustomConfig.getHttpIngressPathList());
        k8sClient.network().ingress().createOrReplace(ingress);
    }

    /**
     * 同一个namespace下，不同 node，会形成不同的集群，此时会发生Ingress资源争抢问题
     * 解决方案：
     *      相同集群 和 namespace下，不同 node，必须使用的不同的域名，否则会出问题
     *
     * @param clusterEntityId
     * @return
     */
    public IngressCustomConfig createIngressCustomConfig(Long clusterEntityId) {
        IngressCustomConfig ingressCustomConfig = new IngressCustomConfig();
        List<HTTPIngressPath> httpIngressPathsList = new ArrayList<>();
        HashMap<String, String> nginxRewriteMap = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        // 同一个集群下只有一个 ingress 配置，不允许多个 ingress 配置
        Optional<ConcurrentHashMap<String, ClusterFlushEntity>> clusterIdEntityMapOptional = taskClusterMapService.getClusterIdEntityMap(clusterEntityId);

        clusterIdEntityMapOptional.ifPresent(clusterIdEntityMap ->
            {
                logger.info("clusterIdEntityMap size is {}",clusterIdEntityMap.size());
                AtomicReference<String> ingressHost = new AtomicReference<>("");
                AtomicReference<String> ingressName = new AtomicReference<>("");
                clusterIdEntityMap.forEach((key, value) -> {
                    IngressRulePath ingressRulePath = new IngressRulePath(value.getNameSpace(),
                                                                          value.getClusterId());
                    stringBuilder.append(
                        ingressRulePath.getNginxReWrite()
                    ).append("\n");
                    httpIngressPathsList.add(ingressRulePath.getHttpIngressPath());
                    ingressHost.set(value.getIngressHost());
                    ingressName.set(value.getIngressName());
                });
                nginxRewriteMap.put(NGINX_REWRITE, stringBuilder.toString());
                ingressCustomConfig.setNginxRewriteMap(nginxRewriteMap);
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
//        String k8sClientKey = clusterFlushEntity.getK8sClientKey();
        Long clusterEntityId = clusterFlushEntity.getClusterEntityId();
        IngressCustomConfig ingressCustomConfig = createIngressCustomConfig(clusterEntityId);
        Optional<KubernetesClient> k8sClientInstance = k8sClusterManager.getK8sClientInstance(clusterEntityId);
        return k8sClientInstance.map(k8sClient -> {
            boolean deploymentExists = k8sClusterManager.isDeploymentExists(
                clusterFlushEntity.getClusterId(),
                clusterFlushEntity.getClusterEntityId());
            // 如果有应用还存在，那么Pod应该还是running
            if (deploymentExists) {
                logger.warn("Find deployment [{}] is still be alive,", clusterFlushEntity.getClusterId());
                k8sClusterManager.deleteDeploymentByClient(k8sClient,clusterFlushEntity.getClusterId());
                logger.warn("Deployment on clusterId [{}]  has been be deleted.", clusterFlushEntity.getClusterId());
                k8sClusterManager.deleteConfigMapsByClient(k8sClient,clusterFlushEntity.getClusterId());
            }
            // configmap 如果还存在，那么直接
            try {
                replaceOrDeleteIngress(k8sClient, ingressCustomConfig);
                return true;
            } catch (Exception e) {
                logger.error("Delete ingress Rule Failed.{}", e.toString());
            }
            return false;
        }).orElse(false);
    }

}
