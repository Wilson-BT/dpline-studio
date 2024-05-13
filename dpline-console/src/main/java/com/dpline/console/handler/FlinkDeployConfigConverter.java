package com.dpline.console.handler;

import com.dpline.common.enums.FileType;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.CollectionUtils;
import com.dpline.common.util.JobConfigSerialization;
import com.dpline.dao.domain.FlinkDeployConfig;
import com.dpline.dao.domain.JarDepend;
import com.dpline.dao.entity.JarFile;
import com.dpline.dao.entity.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FlinkDeployConfigConverter extends DeployConfigConverter {

    private Logger logger = LoggerFactory.getLogger(FlinkDeployConfigConverter.class);

    FlinkDeployConfig flinkDeployConfig;

    public FlinkDeployConfigConverter() {
        flinkDeployConfig = new FlinkDeployConfig();
    }

    /**
     *
     * create all jar
     * @param job
     * @return
     */
    @Override
    public void convertToMotorDeployConfig(Job job) {
        // 查询 job 的任务
        if(Asserts.isNull(job)){
            return;
        }
        flinkDeployConfig.setProjectId(job.getProjectId());
        flinkDeployConfig.setJobId(job.getId());
        flinkDeployConfig.setMotorVersionId(job.getMotorVersionId());
        flinkDeployConfig.setFileType(FileType.of(job.getFileType()));
        // 重新解析 flinkYaml文件 为 map格式，并赋予 RunResourceConfig
        if(flinkDeployConfig.getFileType().equals(FileType.SQL_STREAM)){
            flinkDeployConfig.setSqlText(JobConfigSerialization.unzipJobContent(job.getJobContent()));
        }
    }

    public void mainJar(JarFile mainEffectJar) {
        flinkDeployConfig.setMainJar(new JarDepend(mainEffectJar));
    }
    public void connectorJar(List<JarFile> connectorJars) {
        if(CollectionUtils.isEmpty(connectorJars)){
            return;
        }
        connectorJars.stream().forEach(jarFile -> {
            flinkDeployConfig.getConnectorIdPathMap().put(jarFile.getMainResourceId(),new JarDepend(jarFile));
        });

    }
    public void udfJar(List<JarFile> udfJarFiles) {
        if(CollectionUtils.isEmpty(udfJarFiles)){
            return;
        }
        udfJarFiles.stream().forEach(jarFile -> {
            flinkDeployConfig.getUdfPathMap().put(jarFile.getMainResourceId(),new JarDepend(jarFile));
        });
    }

    public void extendJar(List<JarFile> extendJarFiles) {
        if(CollectionUtils.isEmpty(extendJarFiles)){
            return;
        }
        extendJarFiles.stream().forEach(jarFile -> {
            flinkDeployConfig.getExtendIdPathMap().put(jarFile.getMainResourceId(),new JarDepend(jarFile));
        });
    }
    public FlinkDeployConfig getDeployConfig(){
        return flinkDeployConfig;
    }

}
