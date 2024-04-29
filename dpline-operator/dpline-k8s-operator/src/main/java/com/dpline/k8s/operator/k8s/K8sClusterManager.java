package com.dpline.k8s.operator.k8s;

import com.dpline.common.params.K8sClusterParams;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.EncryptionUtils;
import com.dpline.k8s.operator.job.ClusterFlushEntity;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Component
public class K8sClusterManager {

    private static final String FLINK_POD_TYPE = "flink-native-kubernetes";

    private static final Logger logger = LoggerFactory.getLogger(K8sClusterManager.class);

    // create k8s client
    private static final ConcurrentHashMap<Long, K8sClientLoopList> k8sClientMap = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Long, K8sClusterParams> k8sClusterIdConfigMap = new ConcurrentHashMap<>();

    /**
     * create k8s client，only use at first createK8sClient
     * exist -> return
     * not exist -> put and return
     * @return
     */
    public static String getK8sClientKey(String kubeConfigPath,String nameSpace){
        return EncryptionUtils.getMd5(kubeConfigPath + ":" + nameSpace);
    }

    /**
     * TODO 添加serviceAccount之后，需要解决客户端更新、k8s cluster配置更新的问题
     *
     * @param clusterEntityId
     * @param k8sClusterParams
     * @param number
     * @return
     */
    public Optional<ConcurrentHashMap<Long, K8sClientLoopList>> createK8sClient(Long clusterEntityId, K8sClusterParams k8sClusterParams,int number) {
        if(Asserts.isNull(k8sClusterParams)){
            return Optional.empty();
        }
        File configFile = new File(k8sClusterParams.getKubePath());
        try {
            Config configYaml = Config.fromKubeconfig(String.join("\n", Files.readAllLines(configFile.toPath())));
            configYaml.setTrustCerts(true);
            K8sClientLoopList k8sClientLoopList = new K8sClientLoopList(number);
            IntStream.range(0, number).forEach(num->{
                configYaml.setNamespace(k8sClusterParams.getNameSpace());
                KubernetesClient kubernetesClient = new KubernetesClientBuilder().withConfig(configYaml)
                        .build();
                k8sClientLoopList.add(kubernetesClient);
            });
            // 不存在就创建，存在就直接返回原来的值
            k8sClientMap.computeIfAbsent(clusterEntityId, (v) -> k8sClientLoopList);
            // 保留配置
            k8sClusterIdConfigMap.computeIfAbsent(clusterEntityId,(v) -> k8sClusterParams);
            logger.info("K8s client for path [{}] and namespace [{}] create success", k8sClusterParams.getKubePath(),k8sClusterParams.getNameSpace());
            return Optional.of(k8sClientMap);
        } catch (IOException e) {
            logger.error("K8s client for path [{}] and namespace [{}] create error", k8sClusterParams.getKubePath(),k8sClusterParams.getNameSpace());
            e.printStackTrace();
        }
        return Optional.empty();
    }

//    /**
//     * list all Deployments and get their status
//     *
//     * @param kubernetesClient
//     * @param nameSpace
//     * @return
//     */
//    public List<Tuple2<String, ExecStatus>> listAndConvertDeploymentsStatus(KubernetesClient kubernetesClient, String nameSpace) {
//        DeploymentList deploymentList = kubernetesClient.apps()
//            .deployments()
//            .inNamespace(nameSpace)
//            .withLabel("type", "flink-native-kubernetes")
//            .list();
//        return deploymentList.getItems().stream().map(it -> {
//            System.out.println(it);
//            String name = it.getMetadata().getName();
//            if (it.getStatus().getAvailableReplicas() > 0 || it.getStatus().getReadyReplicas() > 0) {
//                return new Tuple2<String, ExecStatus>(nameSpace + "__" + name, ExecStatus.RUNNING);
//            } else {
//                return new Tuple2<String, ExecStatus>(nameSpace + "__" + name, ExecStatus.FAILING);
//            }
//        }).collect(Collectors.toList());
//    }

    /**
     * Delete k8s client
     *
     * @param
     * @return
     */
    public boolean deleteClientIfExist(Long clusterId) {
        K8sClientLoopList k8sClientLoopList = k8sClientMap.get(clusterId);
        if (Asserts.isNull(k8sClientLoopList)) {
            logger.warn("K8s client is not exists.");
        }
        k8sClientMap.remove(clusterId);
        logger.info("K8s client Id [{}] had been removed", clusterId);
        k8sClusterIdConfigMap.remove(clusterId);
        logger.info("K8s cluster config clusterId:[{}] had been removed",clusterId);
        return true;
    }

    /**
     * Get k8s client by name or path
     *
     * @param
     * @return
     */
    public Optional<KubernetesClient> getK8sClientInstance(Long clusterEntityId) {
        K8sClientLoopList k8sClientLoopList = k8sClientMap.get(clusterEntityId);
        if(Asserts.isNotNull(k8sClientLoopList)){
            return Optional.ofNullable(k8sClientLoopList.poll());
        }
        return Optional.empty();
    }

//    /**
//     * 根据 clusterId 获取 客户端
//     * @param clusterEntityId
//     * @return
//     */
//    public Optional<KubernetesClient> getK8sClientInstance(Long clusterEntityId) {
//        return getK8sClientInstance(clusterEntityId);
//    }

    /**
     * @return
     */
    public boolean isDeploymentExists(ClusterFlushEntity clusterFlushEntity) {
        Optional<KubernetesClient> k8sClientInstance = getK8sClientInstance(clusterFlushEntity.getClusterEntityId());
        return k8sClientInstance.map(kubernetesClient -> kubernetesClient.apps()
            .deployments()
            .inNamespace(clusterFlushEntity.getNameSpace())
            .withLabel("type", FLINK_POD_TYPE)
            .list()
            .getItems()
            .stream().anyMatch(e -> clusterFlushEntity.getClusterId().equals(e.getMetadata().getName()))).orElse(false);
    }


//    public boolean isDeploymentExists(String clusterId,String kubePath,String nameSpace) {
//        Optional<KubernetesClient> k8sClientInstance = getK8sClientInstance(
//            getK8sClientKey(kubePath, nameSpace)
//        );
//        Deployment deployment = k8sClientInstance.map(kubernetesClient -> kubernetesClient.apps()
//            .deployments()
//            .inNamespace(nameSpace)
//            .withName(clusterId)
//            .get()).orElse(null);
//        return Asserts.isNotNull(deployment);
//    }

    public boolean isDeploymentExists(String clusterId,Long clusterEntityId) {
        K8sClusterParams k8sClusterParams = K8sClusterManager.getK8sClusterParams(clusterEntityId);
        if(Asserts.isNull(k8sClusterParams)){
            logger.warn("K8s cluster params is not exists.");
            return false;
        }
        Optional<KubernetesClient> k8sClientInstance = getK8sClientInstance(clusterEntityId);
        Deployment deployment = k8sClientInstance.map(kubernetesClient -> kubernetesClient.apps()
                .deployments()
                .inNamespace(k8sClusterParams.getNameSpace())
                .withName(clusterId)
                .get()).orElse(null);
        return Asserts.isNotNull(deployment);
    }

    /**
     * Get pods
     *
     * @return
     */
    public List<Pod> getPods(ClusterFlushEntity clusterFlushEntity) {
        Optional<KubernetesClient> k8sClientInstance = getK8sClientInstance(clusterFlushEntity.getClusterEntityId());
        return k8sClientInstance.map(k8sClient -> k8sClient.pods()
            .inNamespace(clusterFlushEntity.getNameSpace())
            .withLabels(k8sClient.apps()
                .deployments()
                .inNamespace(clusterFlushEntity.getNameSpace())
                .withName(clusterFlushEntity.getClusterId())
                .get()
                .getSpec()
                .getSelector()
                .getMatchLabels())
            .list().getItems()
        ).orElse(Collections.emptyList());
    }

    public Optional<ContainerStatus> getPodsStatus(List<Pod> podList) {
        Pod pod = podList.stream().findFirst().orElse(null);
        // 如果是空的，就直接
        if (Asserts.isNull(pod)) {
            // pod 是空的
            logger.error("Pod is not exists.");
            return Optional.empty();
        }
        return pod.getStatus().getContainerStatuses().stream().findFirst();
    }

    /**
     * is pod be terminated before
     * unless which status now
     * containerStatus.getTerminated is not null then pod is terminated:true
     * else false.
     *
     * @param
     * @return
     */
    public boolean isPodTerminatedBefore(ContainerStatus containerStatus) {
        return Asserts.isNotNull(containerStatus.getLastState().getTerminated());
    }

    public boolean isPodRunningNow(ContainerStatus containerStatus) {
        return Asserts.isNotNull(containerStatus.getState().getRunning());

    }

//    /**
//     * 生成日志,后面需要修改为实时监控日志
//     *
//     * @return
//     */
//    public String writeDeploymentLog(ClusterFlushEntity clusterInst) {
//        String path = "";
//        try {
//            path = TaskPathResolver.getJobRunErrorLogPath(clusterInst.getK8sClientKey(),clusterInst.getClusterId());
//            String log = getK8sClientInstance(clusterInst.getK8sClientKey())
//                .map(client -> client
//                    .apps()
//                    .deployments()
//                    .inNamespace(clusterInst.getNameSpace())
//                    .withName(clusterInst.getClusterId())
//                    .getLog())
//                .orElse("");
//            Files.write(Paths.get(path), log.getBytes(StandardCharsets.UTF_8));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return path;
//    }

    /**
     * 删除部署
     *
     * @return
     */
    public boolean deleteDeployment(ClusterFlushEntity clusterFlushEntity) {
        Optional<KubernetesClient> k8sClientInstance = getK8sClientInstance(clusterFlushEntity.getClusterEntityId());
        return k8sClientInstance.map(client -> {
            client.apps()
                .deployments()
                .inNamespace(clusterFlushEntity.getNameSpace())
                .withName(clusterFlushEntity.getClusterId())
                .delete();
            return true;
        }).orElse(false);
    }

    public boolean deleteDeploymentByClient(KubernetesClient client, String deploymentName) {
        try{
            client.apps()
                .deployments()
                .withName(deploymentName)
                .delete();
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

//    /**
//     * delete configmaps
//     *
//     * @return
//     */
//    public boolean deleteConfigMaps(ClusterFlushEntity clusterFlushEntity){
//        Optional<KubernetesClient> k8sClientInstance = getK8sClientInstance(clusterFlushEntity.getClusterEntityId());
//        return k8sClientInstance.map(client -> {
//            client.configMaps()
//                .inNamespace(clusterFlushEntity.getNameSpace())
//                .withLabel("type",FLINK_POD_TYPE)
//                .withLabel("app",clusterFlushEntity.getClusterId())
//                .delete();
//            return true;
//        }).orElse(false);
//    }

    public boolean deleteConfigMaps(Long clusterEntityId, String clusterId){
        K8sClusterParams k8sClusterParams = getK8sClusterParams(clusterEntityId);
        if(Asserts.isNull(k8sClusterParams)){
            logger.warn("K8s cluster params is not exists.");
            return false;
        }
        Optional<KubernetesClient> k8sClientInstance = getK8sClientInstance(clusterEntityId);
        return k8sClientInstance.map(client -> {
            client.configMaps()
                .inNamespace(k8sClusterParams.getNameSpace())
                .withLabel("type",FLINK_POD_TYPE)
                .withLabel("app",clusterId)
                .delete();
            return true;
        }).orElse(false);
    }

    public boolean deleteConfigMapsByClient(KubernetesClient k8sClient,String clusterId){
        try{
            k8sClient.configMaps()
                .withLabel("type",FLINK_POD_TYPE)
                .withLabel("app",clusterId)
                .delete();
            logger.info("Configmaps on clusterId [{}] is deleted.",clusterId);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

//
//    public String getDeploymentRestUrl(String clusterId, String nameSpace) {
//        Configuration flinkConfig = new Configuration();
//        flinkConfig.setString(DeploymentOptions.TARGET, RunModeType.K8S_APPLICATION.getValue());
//        flinkConfig.setString(KubernetesConfigOptions.CLUSTER_ID, clusterId);
//        flinkConfig.setString(KubernetesConfigOptions.NAMESPACE, nameSpace);
//        flinkConfig.set(ClientOptions.CLIENT_TIMEOUT, Duration.ofSeconds(FLINK_CLIENT_TIMEOUT_SEC));
//        flinkConfig.set(RestOptions.AWAIT_LEADER_TIMEOUT, FLINK_REST_AWAIT_TIMEOUT_SEC * 1000);
//        flinkConfig.set(RestOptions.RETRY_MAX_ATTEMPTS, FLINK_REST_RETRY_MAX_ATTEMPTS);
//        flinkConfig.setString(KubernetesConfigOptions.NAMESPACE, nameSpace);
//        KubernetesClusterDescriptor clusterDescriptor = new KubernetesClusterClientFactory().createClusterDescriptor(flinkConfig);
//        ClusterClientProvider<String> retrieve = clusterDescriptor.retrieve(clusterId);
//        ClusterClient<String> clusterClient = retrieve.getClusterClient();
//        String webInterfaceURL = clusterClient.getWebInterfaceURL();
//        clusterClient.close();
//        return webInterfaceURL;
//    }

    public static K8sClusterParams getK8sClusterParams(Long clusterId) {
        return k8sClusterIdConfigMap.get(clusterId);
    }

//    /**
//     * 获取 k8s client key
//     * @param clusterId
//     * @return
//     */
//    public static String getK8sClientKey(Long clusterId){
//        K8sClusterParams k8sClusterParams = getK8sClusterParams(clusterId);
//        if(Asserts.isNull(k8sClusterParams)){
//            logger.error("Cluster Id [{}] is not exists.", clusterId.toString());
//        }
//        return getK8sClientKey(
//            k8sClusterParams.getKubePath(),
//            k8sClusterParams.getNameSpace());
//    }



    public void updateK8sClusterIdConfigMap(Long clusterEntityId, K8sClusterParams k8sClusterParams){
        k8sClusterIdConfigMap.compute(clusterEntityId, (v, k) -> k8sClusterParams);
        logger.info("K8sClusterIdConfigMap had been updated");
    }

}



