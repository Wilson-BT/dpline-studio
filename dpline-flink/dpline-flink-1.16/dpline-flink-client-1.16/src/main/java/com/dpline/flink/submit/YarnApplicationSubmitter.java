package com.dpline.flink.submit;


import com.dpline.common.enums.ResponseStatus;
import com.dpline.common.request.*;
import com.dpline.common.util.ExceptionUtil;
import org.apache.flink.client.deployment.*;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptionsInternal;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.configuration.SecurityOptions;
import org.apache.flink.runtime.util.HadoopUtils;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * submit
 */
public class YarnApplicationSubmitter extends AbstractConfigSetting {

    private Logger logger = LoggerFactory.getLogger(YarnApplicationSubmitter.class);

    public SubmitResponse submit(FlinkRequest submitRequest) throws Exception {
        YarnRemoteSubmitRequest yarnRemoteSubmitRequest = (YarnRemoteSubmitRequest) submitRequest;
        // 设置 通用参数
        setGlobalConfig(yarnRemoteSubmitRequest);
        // 设置特殊参数
        setSpecialConfig(configuration, yarnRemoteSubmitRequest);
        UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
        if (HadoopUtils.isKerberosSecurityEnabled(currentUser)) {
            logger.info("kerberos Security is Enabled...");
            Boolean useTicketCache = configuration.get(SecurityOptions.KERBEROS_LOGIN_USETICKETCACHE);
            if(!HadoopUtils.areKerberosCredentialsValid(currentUser, useTicketCache)){
                logger.error("Hadoop security with Kerberos is enabled but the login user [{}] does not have Kerberos credentials or delegation tokens!",currentUser.getUserName());
            }
        }
        logger.info("Task will submitted with configuration as [{}]",configuration.toMap().toString());
        return doSubmit(yarnRemoteSubmitRequest);
    }

    private SubmitResponse doSubmit(YarnRemoteSubmitRequest yarnRemoteSubmitRequest) {
        String clusterId = "";
        String webInterfaceURL = "";
        ResponseStatus responseStatus = ResponseStatus.FAIL;
        ClusterDescriptor<Object> clusterDescriptor = null;
        ClusterClient<Object>  clusterClient = null;
        try {
            DefaultClusterClientServiceLoader defaultClusterClientServiceLoader = new DefaultClusterClientServiceLoader();
            ClusterClientFactory<Object> clusterClientFactory = defaultClusterClientServiceLoader.getClusterClientFactory(configuration);
            clusterDescriptor = clusterClientFactory.createClusterDescriptor(configuration);
            ClusterSpecification clusterSpecification = clusterClientFactory.getClusterSpecification(configuration);
            ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.fromConfiguration(configuration);
            clusterClient = clusterDescriptor.deployApplicationCluster(clusterSpecification, applicationConfiguration)
                    .getClusterClient();
            clusterId = clusterClient.getClusterId().toString();
            webInterfaceURL = clusterClient.getWebInterfaceURL();
            responseStatus = ResponseStatus.SUCCESS;
        } catch (ClusterDeploymentException e) {
            logger.error("Deploy error,{}", ExceptionUtil.exceptionToString(e));
            clusterDescriptor.close();
            clusterClient.close();
            throw new RuntimeException(e);
        }
        logger.info("Task is submitted success for ClusterId:[{}],JobId:[{}]",clusterId,configuration.get(PipelineOptionsInternal.PIPELINE_FIXED_JOB_ID));
        return new SubmitResponse(yarnRemoteSubmitRequest.getJobDefinitionOptions().getJobId(), clusterId,
                configuration.get(RestOptions.PORT),
                responseStatus,
                configuration.get(PipelineOptionsInternal.PIPELINE_FIXED_JOB_ID),
                webInterfaceURL
        );

    }
    /**
     * yarn 特殊的写死的配置
     *
     * @param configuration
     * @param submitRequest
     */
    @Override
    public void setSpecialConfig(Configuration configuration, FlinkSubmitRequest submitRequest) {
        YarnRemoteSubmitRequest yarnRemoteSubmitRequest = (YarnRemoteSubmitRequest) submitRequest;
        // 所有资源的资源路径
        // checkpoint 的卡点,固定写法
        configuration.set(ExecutionCheckpointingOptions.EXTERNALIZED_CHECKPOINT, CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        configuration
            // yarn.provided.lib.dirs
            .set(YarnConfigOptions.PROVIDED_LIB_DIRS, yarnRemoteSubmitRequest.getYarnOptions().getFlinkJarDirPath())
            // flinkDistJar
            .set(YarnConfigOptions.FLINK_DIST_JAR, yarnRemoteSubmitRequest.getYarnOptions().getFlinkDistJarPath())
            // yarn application name
            .set(YarnConfigOptions.APPLICATION_NAME, submitRequest.getJobDefinitionOptions().getJobName())
            // yarn application Type
            .set(YarnConfigOptions.APPLICATION_TYPE, submitRequest.getRunModeType().getValue());
    }

}
