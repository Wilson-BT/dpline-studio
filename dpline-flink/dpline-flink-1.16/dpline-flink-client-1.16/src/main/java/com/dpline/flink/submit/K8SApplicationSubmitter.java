package com.dpline.flink.submit;


import com.dpline.common.enums.ExposedType;
import com.dpline.common.enums.ResponseStatus;
import com.dpline.common.params.K8sOptions;
import com.dpline.common.params.RuntimeOptions;
import com.dpline.common.request.FlinkK8sRemoteSubmitRequest;
import com.dpline.common.request.FlinkRequest;
import com.dpline.common.request.SubmitResponse;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.StringUtils;
import com.dpline.common.util.TaskPathResolver;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.configuration.*;
import org.apache.flink.configuration.description.Description;
import org.apache.flink.kubernetes.KubernetesClusterClientFactory;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.flink.configuration.ConfigOptions.key;
import static org.apache.flink.configuration.description.TextElement.code;


/**
 * submit
 */
public class K8SApplicationSubmitter extends AbstractConfigSetting {


    private Logger logger = LoggerFactory.getLogger(K8SApplicationSubmitter.class);

    private KubernetesClusterDescriptor clusterDescriptor;

    private ClusterSpecification clusterSpecification;

    public static final ConfigOption<String> MASTER_ENV_CLUSTER_ID =
        key(ResourceManagerOptions.CONTAINERIZED_MASTER_ENV_PREFIX + "CLUSTER_ID")
            .stringType()
            .noDefaultValue()
            .withDescription(
                Description.builder()
                    .text(
                        "The cluster-id, which should be no more than 45 characters, is used for identifying a unique Flink cluster. "
                            + "The id must only contain lowercase alphanumeric characters and \"-\". "
                            + "The required format is %s. "
                            + "If not set, the client will automatically generate it with a random ID.",
                        code("[a-z]([-a-z0-9]*[a-z0-9])"))
                    .build());

    public static final ConfigOption<String> TASK_ENV_CLUSTER_ID =
        key(ResourceManagerOptions.CONTAINERIZED_TASK_MANAGER_ENV_PREFIX + "CLUSTER_ID")
            .stringType()
            .noDefaultValue()
            .withDescription(
                Description.builder()
                    .text(
                        "The cluster-id, which should be no more than 45 characters, is used for identifying a unique Flink cluster. "
                            + "The id must only contain lowercase alphanumeric characters and \"-\". "
                            + "The required format is %s. "
                            + "If not set, the client will automatically generate it with a random ID.",
                        code("[a-z]([-a-z0-9]*[a-z0-9])"))
                    .build());



    public SubmitResponse submit(FlinkRequest submitRequest) throws Exception {
        FlinkK8sRemoteSubmitRequest flinkRemoteSubmitRequest = (FlinkK8sRemoteSubmitRequest) submitRequest;
        K8sOptions k8sOptions =  flinkRemoteSubmitRequest.getK8sOptions();
        // 设置 通用参数
        setGlobalConfig(flinkRemoteSubmitRequest);
        // 设置特殊参数
        setSpecialConfig(configuration, flinkRemoteSubmitRequest);
        logger.info("Task will submitted with configuration as [{}]",configuration.toMap().toString());
        getK8sClusterDescriptorAndSpecification(configuration);
        ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.fromConfiguration(configuration);
        clusterDescriptor.deployApplicationCluster(clusterSpecification, applicationConfiguration);
        logger.info("Task is submitted success for ClusterId:[{}]",k8sOptions.getClusterId());
        return new SubmitResponse(flinkRemoteSubmitRequest.getJobDefinitionOptions().getJobId(),
            k8sOptions.getClusterId(),
            configuration.get(RestOptions.PORT),
            ResponseStatus.SUCCESS,
            configuration.get(PipelineOptionsInternal.PIPELINE_FIXED_JOB_ID));
    }

    private void getK8sClusterDescriptorAndSpecification(Configuration configuration) {
        KubernetesClusterClientFactory clientFactory = new KubernetesClusterClientFactory();
        this.clusterDescriptor = clientFactory.createClusterDescriptor(configuration);
        this.clusterSpecification = clientFactory.getClusterSpecification(configuration);
    }


    /**
     * K8s 特殊的写死的配置
     *
     * @param configuration
     * @param submitRequest
     */
    @Override
    public void setSpecialConfig(Configuration configuration, FlinkK8sRemoteSubmitRequest submitRequest) {
        // 镜像拉取模式
        K8sOptions k8sOptions = submitRequest.getK8sOptions();
        // resource 资源设置
        RuntimeOptions resourceOptions = submitRequest.getRuntimeOptions();
        // 始终拉取镜像写死
        configuration.set(KubernetesConfigOptions.CONTAINER_IMAGE_PULL_POLICY, KubernetesConfigOptions.ImagePullPolicy.Always);
        // clusterId
        configuration.set(KubernetesConfigOptions.CLUSTER_ID,k8sOptions.getClusterId());
        // 设置 jobmanager环境变量，方便 日志获取
        configuration.set(MASTER_ENV_CLUSTER_ID,k8sOptions.getClusterId());
        // 设置 taskmanager环境变量，方便日志获取
        configuration.set(TASK_ENV_CLUSTER_ID,k8sOptions.getClusterId());
        // 设置镜像位置
        configuration.set(KubernetesConfigOptions.CONTAINER_IMAGE,k8sOptions.getImageAddress());
        configuration.set(KubernetesConfigOptions.NAMESPACE,k8sOptions.getNameSpace());
//        if(Asserts.isNotNull(k8sOptions.getSelectNode())){
//            configuration.set(KubernetesConfigOptions.JOB_MANAGER_NODE_SELECTOR,k8sOptions.getSelectNode());
//            configuration.set(KubernetesConfigOptions.TASK_MANAGER_NODE_SELECTOR,k8sOptions.getSelectNode());
//        }
        if (StringUtils.isNotEmpty(k8sOptions.getKubePath())){
            configuration.set(KubernetesConfigOptions.KUBE_CONFIG_FILE,k8sOptions.getKubePath());
        }
        configuration.set(KubernetesConfigOptions.KUBERNETES_POD_TEMPLATE,k8sOptions.getPodFilePath());
        // checkpoint 的卡点,固定写法
        configuration.set(ExecutionCheckpointingOptions.EXTERNALIZED_CHECKPOINT, CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        ExposedType exposedType = k8sOptions.getExposedType();

        // 默认为 clusterIp
//        if (Asserts.isNull(exposedType)) {
//            exposedType = ExposedType.CLUSTER_IP;
//        }
        configuration.set(KubernetesConfigOptions.REST_SERVICE_EXPOSED_TYPE, convertExposedTypeToFlinkConfig(exposedType));
        double jobManagerCpu = resourceOptions.getJobManagerCpu();
        double taskManagerCpu = resourceOptions.getTaskManagerCpu();
        if (!Asserts.isZero(jobManagerCpu)) {
            configuration.set(KubernetesConfigOptions.JOB_MANAGER_CPU, jobManagerCpu);
        }
        if (!Asserts.isZero(taskManagerCpu)){
            configuration.set(KubernetesConfigOptions.TASK_MANAGER_CPU, taskManagerCpu);
        }
    }


    KubernetesConfigOptions.ServiceExposedType convertExposedTypeToFlinkConfig(ExposedType exposedType) {
        switch (exposedType) {
            case NODE_PORT:
                return KubernetesConfigOptions.ServiceExposedType.NodePort;
            case REBALANCE_PORT:
                return KubernetesConfigOptions.ServiceExposedType.LoadBalancer;
            case CLUSTER_IP:
                return KubernetesConfigOptions.ServiceExposedType.ClusterIP;
        }
        return KubernetesConfigOptions.ServiceExposedType.NodePort;
    }
}
