package com.dpline.flink.submit;

import com.dpline.common.params.RuntimeOptions;
import com.dpline.common.request.FlinkK8sRemoteSubmitRequest;
import com.dpline.common.request.FlinkRequest;
import com.dpline.common.request.FlinkSubmitRequest;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.StringUtils;
import com.dpline.common.util.TaskPathResolver;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.configuration.*;

import java.util.*;

public abstract class AbstractConfigSetting {

    Configuration configuration;

    private FlinkSubmitRequest submitRequest;

    /**
     * 通用配置 初始化 params > flinkYaml > flink-conf
     */
    public Configuration setGlobalConfig(FlinkSubmitRequest submitRequest) {
        this.submitRequest = submitRequest;
        loadConfiguration();
        // 其他p运行设置参数
        setFlinkYamlOptions();
        // 任务定义参数
        setNecessaryFlinkConfig();
        // class 类的参数
        setAppArgs();
        // 运行资源参数
        setResourceOption();

        return this.configuration;
    }


    /**
     * load flink config
     */
    private Configuration loadConfiguration() {
        String localFlinkConfPath = TaskPathResolver.getLocalFlinkConfPath(submitRequest.getFlinkHomeOptions().getFlinkPath());
        this.configuration = GlobalConfiguration.loadConfiguration(localFlinkConfPath);
        if (!configuration.contains(DeploymentOptionsInternal.CONF_DIR)) {
            configuration.set(DeploymentOptionsInternal.CONF_DIR, localFlinkConfPath);
        }
        return this.configuration;
    }

    private Configuration setFlinkYamlOptions() {
        Map<String, String> newMap = new HashMap<>();
        // 合并参数
        Map<String, String> configurationMap = configuration.toMap();
        this.submitRequest.getRuntimeOptions().getOtherParams().forEach((key, value) -> {
            if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value.toString())) {
                configurationMap.put(key, value.toString());
            }
        });
        // 替换参数
        configurationMap.forEach((key,value)->{
            if(Asserts.isNull(key) || Asserts.isNull(value)){
                return;
            }
            if(value.contains("${jobName}")){
                newMap.put(key,
                        value.replace("${jobName}", this.submitRequest.getJobDefinitionOptions().getJobName()));
            } else {
                newMap.put(key,value);
            }
        });
        // 重新赋值
        this.configuration = Configuration.fromMap(newMap);
        return this.configuration;
    }


    /**
     * 应用的一部分参数
     * @return
     */
    private Configuration setAppArgs() {
        List<String> appArgs = this.submitRequest.getJobDefinitionOptions().getAppArgs();
        if (appArgs == null || appArgs.size() == 0) {
            return this.configuration;
        }
        configuration.set(ApplicationConfiguration.APPLICATION_ARGS, appArgs);
        return this.configuration;
    }

    /**
     * 设置资源
     */
    private void setResourceOption() {
        RuntimeOptions resourceOptions = this.submitRequest.getRuntimeOptions();
        // 内存资源、cpu资源
        if (StringUtils.isNotEmpty(resourceOptions.getJobManagerMem())) {
            configuration.set(JobManagerOptions.TOTAL_PROCESS_MEMORY,
                new MemorySize(MemorySize.parseBytes(resourceOptions.getJobManagerMem()))
            );
        }
        int parallelism = resourceOptions.getParallelism();
        if (StringUtils.isNotEmpty(resourceOptions.getTaskManagerMem())) {
            configuration.set(TaskManagerOptions.TOTAL_PROCESS_MEMORY,
                new MemorySize(MemorySize.parseBytes(resourceOptions.getTaskManagerMem()))
            );
        }
        if (!Asserts.isZero(parallelism)) {
            configuration.set(CoreOptions.DEFAULT_PARALLELISM, parallelism);
        }
    }



    /**
     * 任务运行必要的参数
     */
    private Configuration setNecessaryFlinkConfig() {
        // 设置默认的 checkpoint 地址
        configuration.set(CheckpointingOptions.CHECKPOINTS_DIRECTORY, this.submitRequest.getJobDefinitionOptions().getDefaultCheckPointDir());
        // 任务执行模式
        configuration.set(DeploymentOptions.TARGET, this.submitRequest.getRunModeType().getValue());
        // 任务名称
        configuration.set(PipelineOptions.NAME, this.submitRequest.getJobDefinitionOptions().getJobName());
        // local jar包 地址
        configuration.set(PipelineOptions.JARS, new ArrayList<String>(Collections.singleton(submitRequest.getJobDefinitionOptions().getJarPath())));
        // 主类名
        configuration.set(ApplicationConfiguration.APPLICATION_MAIN_CLASS, this.submitRequest.getJobDefinitionOptions().getMainClass());
        // job id
        configuration.set(PipelineOptionsInternal.PIPELINE_FIXED_JOB_ID, this.submitRequest.getJobDefinitionOptions().getRunJobId());
        return this.configuration;
    }

    /**
     * 个性化参数设置
     *
     * @param configuration
     */
    public abstract void setSpecialConfig(Configuration configuration, FlinkSubmitRequest flinkSubmitRequest);


}
