package com.handsome.operator.service;

import com.handsome.common.Constants;
import com.handsome.common.util.Asserts;
import com.handsome.dao.mapper.FlinkSessionMapper;
import com.handsome.common.util.TaskPath;
import com.handsome.operator.entry.IngressRulePath;
import com.handsome.operator.job.ClusterFlushEntity;
import com.handsome.operator.k8s.K8sClientManager;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.networking.v1beta1.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskIngressService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(TaskIngressService.class);


    @Autowired
    FlinkSessionMapper flinkSessionMapper;


    @Autowired
    K8sClientManager k8sClientManager;

    @Autowired
    ConcurrentHashMap<String, ConcurrentHashMap<String, ClusterFlushEntity>> clientKubePathMap;


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
    public void setK8sClientManager(K8sClientManager k8sClientManager) {
        this.k8sClientManager = k8sClientManager;
    }

    /**
     * 创建ingress，
     * 在创建session的时候调用
     * 或者创建任务的时候调用
     *
     */
    public synchronized void addIngressRule(ClusterFlushEntity clusterFlushEntity) {
        logger.info("ClusterId : [{}] has been add into {clusterId => IngressPath} Map", clusterFlushEntity.getClusterId());
        addIngressRuleAndAnnotations(clusterFlushEntity.getK8sClientKey(),
                                     clusterFlushEntity.getClusterId(),
                                     clusterFlushEntity.getIngressRulePath());
    }

    /**
     * 只在删除 session 的时候外部调用
     *
     * @param clusterId
     * @param nameSpace
     * @param kubePath
     */
    public synchronized boolean delIngress(String clusterId,
                                           String nameSpace,
                                           String kubePath) {
        String k8sClientKey = K8sClientManager.getK8sClientKey(kubePath, nameSpace);
        ConcurrentHashMap<String, ClusterFlushEntity> clusterIdEntityMap = clientKubePathMap.get(k8sClientKey);
        ClusterFlushEntity removedIngressRulePath = clusterIdEntityMap.remove(clusterId);
        return deleteIngressRuleAndAnnotations(k8sClientKey, clusterId, removedIngressRulePath.getIngressRulePath());
    }


    /**
     * 用于监控的时候，任务一旦停止，需要删除ingress
     *
     * @param clusterId
     * @param k8sClientKey
     * @param clusterFlushEntity
     * @return
     */
    public synchronized boolean delIngressRule(String clusterId,
                                           String k8sClientKey,
                                           ClusterFlushEntity clusterFlushEntity) {
        return deleteIngressRuleAndAnnotations(k8sClientKey, clusterId, clusterFlushEntity.getIngressRulePath());
    }

    /**
     * 初次创建ingress,需要遍历nameSpace下所有的规则
     *
     * @return
     */
    public synchronized boolean createIngress(String kubePathKey, Map<String, String> nginxRewriteMap, KubernetesClient k8sClient) {
        ArrayList<HTTPIngressPath> httpIngressPathList = new ArrayList<>();
        ConcurrentHashMap<String, ClusterFlushEntity> clusterIdEntityMap = clientKubePathMap.get(kubePathKey);
        Ingress ingress = null;
        try {
            clusterIdEntityMap.forEach((key, value) -> {
                httpIngressPathList.add(value.getIngressRulePath().getHttpIngressPath());
            });
            ArrayList<IngressRule> ingressRules = new ArrayList<>();
            ingressRules.add(
                new IngressRule(Constants.INGRESS_HOST,
                    new HTTPIngressRuleValueBuilder()
                        .withPaths(httpIngressPathList)
                        .build()));
            ingress =
                new IngressBuilder()
                    .withApiVersion(Constants.K8S_INGRESS_VERSION)
                    .withNewMetadata()
                    .withName(Constants.K8S_INGRESS_NAME)
                    .withAnnotations(nginxRewriteMap)
                    .endMetadata()
                    .withNewSpec()
                    .withRules(ingressRules)
                    .endSpec()
                    .build();
            k8sClient.network().ingresses().resource(ingress).createOrReplace();
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
     * @param k8sClientKey
     * @param clusterId
     * @param ingressRulePath
     * @return
     */
    private synchronized boolean addIngressRuleAndAnnotations(String k8sClientKey,
                                                              String clusterId,
                                                              IngressRulePath ingressRulePath) {
        Optional<KubernetesClient> k8sClientInstance = k8sClientManager.getK8sClientInstance(k8sClientKey);
        // 此处不需要过滤掉指定的clusterId，即需要全部创建
        Map<String, String> nginxRewriteMap = createNewAno(k8sClientKey, "");
        return k8sClientInstance.map(k8sClient -> {
            final Deployment deployment =
                k8sClient
                    .apps()
                    .deployments()
                    .withName(clusterId)
                    .get();
            if (deployment == null) {
                logger.warn("While create cluster ingress, Could not find deployment {}", clusterId);
            }
            // Get com.handsome.operator deploy
            try {
                doIngressAddWork(k8sClient, nginxRewriteMap, ingressRulePath);
                return true;
            } catch (Exception e) {
                logger.warn("Add ingress rule error");
                e.printStackTrace();
                return createIngress(k8sClientKey, nginxRewriteMap, k8sClient);
            }
        }).orElse(false);

    }

    private void doIngressAddWork(KubernetesClient k8sClient, Map<String, String> nginxRewriteMap, IngressRulePath ingressRulePath) {
        k8sClient.network()
            .ingresses()
            .withName(Constants.K8S_INGRESS_NAME)
            .edit(
                i -> new IngressBuilder(i)
                    .editMetadata()
                    .withAnnotations(nginxRewriteMap)
                    .endMetadata()
                    .editSpec()
                    .editLastRule()
                    .editHttp()
                    .addToPaths(ingressRulePath.getHttpIngressPath())
                    .endHttp()
                    .endRule()
                    .endSpec()
                    .build()
            );
    }

    public Map<String, String> createNewAno(String k8sClientKey, String clusterId) {
        HashMap<String, String> nginxRewriteMap = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        ConcurrentHashMap<String, ClusterFlushEntity> clusterIdEntityMap = clientKubePathMap.get(k8sClientKey);
        if(Asserts.isNotNull(clusterIdEntityMap)){
            clusterIdEntityMap.forEach((key, value) -> {
                String nginxReWrite = value.getIngressRulePath().getNginxReWrite();
                if (!key.equals(clusterId)) {
                    stringBuilder.append(nginxReWrite).append("\n");
                }
            });
        }
        nginxRewriteMap.put(Constants.NGINX_REWRITE, stringBuilder.toString());
        return nginxRewriteMap;
    }


    /**
     * 删除ingress，如果 deployment 存在，需要同时删除掉deployment
     * @param k8sClientKey
     * @param clusterId
     * @param removedIngressRulePath
     * @return
     */
    private synchronized boolean deleteIngressRuleAndAnnotations(String k8sClientKey,
                                                                 String clusterId,
                                                                 IngressRulePath removedIngressRulePath) {
        // 此处需要过滤掉指定的clusterId，只需要单独删除，如果删除失败
        Map<String, String> nginxRewriteMap = createNewAno(k8sClientKey, clusterId);
        Optional<KubernetesClient> k8sClientInstance = k8sClientManager.getK8sClientInstance(k8sClientKey);
        return k8sClientInstance.map(k8sClient -> {
            final Deployment deployment =
                k8sClient
                    .apps()
                    .deployments()
                    .withName(clusterId)
                    .get();
            if (deployment != null) {
                logger.warn("Find deployment [{}] is still be alive,", clusterId);
                k8sClientManager.deleteDeploymentByClient(k8sClient,clusterId);
                logger.warn("Deployment on clusterId [{}]  has been be deleted.", clusterId);
            }
            // configmaps 如果还存在的话，需要删除
            k8sClientManager.deleteConfigMapsByClient(k8sClient,clusterId);

            try {
                doIngressDeleteWork(k8sClient, nginxRewriteMap, removedIngressRulePath);
                return true;
            } catch (Exception e) {
                logger.error("Delete ingress Rule Failed.{}", e.toString());
                try {
                    deleteIngress(k8sClient,clusterId);
                } catch (Exception ex){
                    logger.error("Try to delete ingress Failed.{}", e.toString());
                    ex.printStackTrace();
                }
                return true;
            }
        }).orElse(false);
    }

    private void deleteIngress(KubernetesClient k8sClient,String clusterId) {
        Ingress ingress = k8sClient.network()
            .ingresses()
            .withName(Constants.K8S_INGRESS_NAME)
            .get();
        List<IngressRule> rules = ingress.getSpec().getRules();
        List<HTTPIngressPath> paths = rules.get(0).getHttp().getPaths();
        if(paths.size() == 1
            && paths.get(0).getBackend().getServiceName()
                .equals(TaskPath.getServiceName(clusterId))){
            k8sClient.network()
                .ingresses()
                .withName(Constants.K8S_INGRESS_NAME)
                .delete();
            logger.warn("Ingress has only one path [{}], and the path need deleted, so ingress has been deleted.",clusterId);
        }
    }

    /**
     * 当只剩下最后一个任务的时候删除不掉，此时直接删除ingress
     *
     * @param k8sClient
     * @param nginxRewriteMap
     * @param ingressRulePath
     */
    private void doIngressDeleteWork(KubernetesClient k8sClient,
                                     Map<String, String> nginxRewriteMap,
                                     IngressRulePath ingressRulePath) {
        HTTPIngressPath httpIngressPath = ingressRulePath.getHttpIngressPath();
        k8sClient.network()
            .ingresses()
            .withName(Constants.K8S_INGRESS_NAME)
            .edit(
                i -> new IngressBuilder(i)
                    .editMetadata()
                    .withAnnotations(nginxRewriteMap)
                    .endMetadata()
                    .editSpec()
                    .editLastRule()
                    .editHttp()
                    .removeFromPaths(httpIngressPath)
                    .endHttp()
                    .endRule()
                    .endSpec()
                    .build()
            );
    }
}
