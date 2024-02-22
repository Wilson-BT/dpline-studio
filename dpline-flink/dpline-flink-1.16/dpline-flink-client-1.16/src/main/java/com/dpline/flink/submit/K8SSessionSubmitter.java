package com.dpline.flink.submit;

import com.dpline.common.params.K8sOptions;
import com.dpline.common.request.FlinkK8sRemoteSubmitRequest;
import com.dpline.common.request.FlinkRequest;
import com.dpline.common.util.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.dpline.common.enums.ResponseStatus;
import com.dpline.common.request.SubmitResponse;
import lombok.Builder;
import lombok.Data;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * 直接 run jobId
 *
 */
public class K8SSessionSubmitter extends AbstractConfigSetting {

    private final static Logger logger = LoggerFactory.getLogger(K8SSessionSubmitter.class);

    @Override
    public void setSpecialConfig(Configuration configuration, FlinkK8sRemoteSubmitRequest submitRequest) {
    }

    public SubmitResponse submit(FlinkRequest submitRequest) throws Exception {
        FlinkK8sRemoteSubmitRequest flinkRemoteSubmitRequest = (FlinkK8sRemoteSubmitRequest) submitRequest;
        K8sOptions k8sOptions = flinkRemoteSubmitRequest.getK8sOptions();
        configuration = setGlobalConfig(flinkRemoteSubmitRequest);
        String argStr = null;
        List<String> args = configuration.get(ApplicationConfiguration.APPLICATION_ARGS);
        if(CollectionUtils.isNotEmpty(args)){
            argStr = String.join(" ", args);
        }

        JarRunRequest jarRunRequest = JarRunRequest.builder()
            .allowNonRestoredState(configuration.get(SavepointConfigOptions.SAVEPOINT_IGNORE_UNCLAIMED_STATE))
            .entryClass(configuration.get(ApplicationConfiguration.APPLICATION_MAIN_CLASS))
            .savepointPath(configuration.get(SavepointConfigOptions.SAVEPOINT_PATH))
            .parallelism(String.valueOf(configuration.get(CoreOptions.DEFAULT_PARALLELISM)))
            .programArgs(argStr)
            .build();
        String jarRunRequestStr = JSONUtils.toJsonString(jarRunRequest);
        // session need session Id
        String restUrlPath = TaskPathResolver.getNewRestUrlPath(k8sOptions.getNameSpace(),k8sOptions.getIngressHost(),k8sOptions.getSessionName());
        String restJarRunUrlPath = String.format("%s/jars/%s/run", restUrlPath, flinkRemoteSubmitRequest.getJobDefinitionOptions().getDeployAddress());
        String responseContent = HttpUtils.doStringBodyPost(restJarRunUrlPath, jarRunRequestStr);
        if (StringUtils.isNotEmpty(responseContent)){
            ObjectNode jsonNodes = JSONUtils.parseObject(responseContent);
            String jobId = jsonNodes.get("jobid").textValue();
            return new SubmitResponse(
                flinkRemoteSubmitRequest.getJobDefinitionOptions().getJobId(),
                k8sOptions.getSessionName(),
                configuration.get(RestOptions.PORT),
                ResponseStatus.SUCCESS,
                jobId);
        }else {
            logger.error("Remote for rest-url: {} failed, jar run request params: {}",restUrlPath,jarRunRequestStr);
        }
        return null;
    }

    @Data
    @Builder
    static class JarRunRequest implements Serializable {

        String entryClass;

        String programArgs;

        String parallelism;

        String savepointPath;

        boolean allowNonRestoredState;

    }
}
