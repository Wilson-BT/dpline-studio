package com.handsome.operator.k8s;

import com.handsome.common.enums.RunModeType;
import com.handsome.common.util.Asserts;
import com.handsome.common.util.EncryptionUtils;
import com.handsome.dao.entity.K8sNameSpace;
import com.handsome.common.util.TaskPath;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.*;
import org.apache.flink.client.cli.ClientOptions;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.kubernetes.KubernetesClusterClientFactory;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Component
public class K8sClientManager {

    // see org.apache.flink.client.cli.ClientOptions.CLIENT_TIMEOUT}
    private long FLINK_CLIENT_TIMEOUT_SEC = 30L;
    // see org.apache.flink.configuration.RestOptions.AWAIT_LEADER_TIMEOUT
    private long FLINK_REST_AWAIT_TIMEOUT_SEC = 10L;
    // see org.apache.flink.configuration.RestOptions.RETRY_MAX_ATTEMPTS
    private int FLINK_REST_RETRY_MAX_ATTEMPTS = 2;

    private static final String FLINK_POD_TYPE = "flink-native-kubernetes";

    private static final Logger logger = LoggerFactory.getLogger(K8sClientManager.class);

    // create k8s client
    private static final ConcurrentHashMap<String, K8sClientLoopList> k8sClientMap = new ConcurrentHashMap<>();

    /**
     * create k8s client，only use at first createK8sClient
     * exist -> return
     * not exist -> put and return
     * @return
     */
    public static String getK8sClientKey(String kubeConfigPath,String nameSpace){
        return EncryptionUtils.getMd5(kubeConfigPath + ":" + nameSpace);
    }

    public Optional<ConcurrentHashMap<String, K8sClientLoopList>> createK8sClient(K8sNameSpace k8sNameSpace, int number) {
        String md5Key = getK8sClientKey(k8sNameSpace.getKubePath(),k8sNameSpace.getNameSpace());
        File configFile = new File(k8sNameSpace.getKubePath());
        try {
            Config configYaml = Config.fromKubeconfig(String.join("\n", Files.readAllLines(configFile.toPath())));
            configYaml.setTrustCerts(true);
            K8sClientLoopList k8sClientLoopList = new K8sClientLoopList(number);
            IntStream.range(0, number).forEach(num->{
                k8sClientLoopList.add(new DefaultKubernetesClient(configYaml));
            });
            k8sClientMap.computeIfAbsent(md5Key, (v) -> k8sClientLoopList);
            return Optional.of(k8sClientMap);
        } catch (IOException e) {
            logger.error("K8s client for path [{}] and namespace [{}] create error", k8sNameSpace.getKubePath(),k8sNameSpace.getNameSpace());
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
    public void deleteClientIfExist(String nameSpace,String kubePath) {
        String md5Key = getK8sClientKey(kubePath, nameSpace);
        K8sClientLoopList k8sClientLoopList = k8sClientMap.get(md5Key);
        if (Asserts.isNull(k8sClientLoopList)) {
            return;
        }
        k8sClientMap.remove(md5Key);
    }

    /**
     * Get k8s client by name or path
     *
     * @param
     * @return
     */
    public Optional<KubernetesClient> getK8sClientInstance(String k8sClientKey) {
//        String md5Key = getK8sClientKey(kubePath, nameSpace);
        return Optional.ofNullable(k8sClientMap.get(k8sClientKey).poll());
    }

    /**
     * @param clusterId
     * @return
     */
    public boolean isDeploymentExists(String k8sClientKey, String clusterId, String nameSpace) {
        Optional<KubernetesClient> k8sClientInstance = getK8sClientInstance(k8sClientKey);
        return k8sClientInstance.map(kubernetesClient -> kubernetesClient.apps()
            .deployments()
            .inNamespace(nameSpace)
            .withLabel("type", FLINK_POD_TYPE)
            .list()
            .getItems()
            .stream().anyMatch(e -> clusterId.equals(e.getMetadata().getName()))).orElse(false);
    }

    /**
     * Get pods
     *
     * @param k8sClientKey
     * @param deploymentName
     * @param nameSpace
     * @return
     */
    public List<Pod> getPods(String k8sClientKey, String deploymentName, String nameSpace) {
        Optional<KubernetesClient> k8sClientInstance = getK8sClientInstance(k8sClientKey);
        return k8sClientInstance.map(k8sClient -> k8sClient.pods()
            .inNamespace(nameSpace)
            .withLabels(k8sClient.apps()
                .deployments()
                .inNamespace(nameSpace)
                .withName(deploymentName)
                .get()
                .getSpec()
                .getSelector()
                .getMatchLabels()).list().getItems()
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
    public boolean isPodTerminatedBefore(Optional<ContainerStatus> podsStatus) {
        return podsStatus.map(containerStatus ->
            Asserts.isNotNull(containerStatus.getLastState().getTerminated()))
            .orElse(false);
    }

    public boolean isPodRunningNow(Optional<ContainerStatus> podsStatus) {
        return podsStatus.map(containerStatus ->
            Asserts.isNotNull(containerStatus.getState().getRunning()))
            .orElse(false);
    }


    /**
     * 生成日志,后面需要修改为实时监控日志
     *
     * @return
     */
    public String writeDeploymentLog(String clientKey, String nameSpace, String deploymentName) {
        String path = "";
        try {
            // TODO 将 日志打印在当前目录下的 app_home/log/project_code/task_name_date_time.log
            String localProjectHomeLogPath = TaskPath.getLocalProjectHomeLogPath();
            path = String.format("%s/%s_%s.log", localProjectHomeLogPath, nameSpace, deploymentName);
            String log = getK8sClientInstance(clientKey)
                .map(client -> client.apps()
                    .deployments()
                    .inNamespace(nameSpace)
                    .withName(deploymentName).getLog()).orElse("");
            Files.write(Paths.get(path), log.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 删除部署
     *
     * @param nameSpace
     * @param deploymentName
     * @return
     */
    public boolean deleteDeployment(String clientKey, String nameSpace, String deploymentName) {
        Optional<KubernetesClient> k8sClientInstance = getK8sClientInstance(clientKey);
        return k8sClientInstance.map(client -> {
            client.apps()
                .deployments()
                .inNamespace(nameSpace)
                .withName(deploymentName)
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

    /**
     * delete configmaps
     *
     * @param clientKey
     * @param nameSpace
     * @param deploymentName
     * @return
     */
    public boolean deleteConfigMaps(String clientKey, String nameSpace, String deploymentName){
        Optional<KubernetesClient> k8sClientInstance = getK8sClientInstance(clientKey);
        return k8sClientInstance.map(client -> {
            client.configMaps()
                .inNamespace(nameSpace)
                .withLabel("type",FLINK_POD_TYPE)
                .withLabel("app",deploymentName)
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


    public String getDeploymentRestUrl(String clusterId, String nameSpace) {
        Configuration flinkConfig = new Configuration();
        flinkConfig.setString(DeploymentOptions.TARGET, RunModeType.K8S_APPLICATION.getValue());
        flinkConfig.setString(KubernetesConfigOptions.CLUSTER_ID, clusterId);
        flinkConfig.setString(KubernetesConfigOptions.NAMESPACE, nameSpace);
        flinkConfig.set(ClientOptions.CLIENT_TIMEOUT, Duration.ofSeconds(FLINK_CLIENT_TIMEOUT_SEC));
        flinkConfig.set(RestOptions.AWAIT_LEADER_TIMEOUT, FLINK_REST_AWAIT_TIMEOUT_SEC * 1000);
        flinkConfig.set(RestOptions.RETRY_MAX_ATTEMPTS, FLINK_REST_RETRY_MAX_ATTEMPTS);
        flinkConfig.setString(KubernetesConfigOptions.NAMESPACE, nameSpace);
        KubernetesClusterDescriptor clusterDescriptor = new KubernetesClusterClientFactory().createClusterDescriptor(flinkConfig);
        ClusterClientProvider<String> retrieve = clusterDescriptor.retrieve(clusterId);
        ClusterClient<String> clusterClient = retrieve.getClusterClient();
        String webInterfaceURL = clusterClient.getWebInterfaceURL();
        clusterClient.close();
        return webInterfaceURL;
    }

}



