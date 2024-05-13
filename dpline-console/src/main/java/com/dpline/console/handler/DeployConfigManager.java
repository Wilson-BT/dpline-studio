package com.dpline.console.handler;

import com.dpline.common.enums.JarType;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.CollectionUtils;
import com.dpline.common.util.JSONUtils;
import com.dpline.console.service.impl.JarFileServiceImpl;
import com.dpline.dao.domain.AbstractDeployConfig;
import com.dpline.dao.domain.SourceConfig;
import com.dpline.dao.entity.JarFile;
import com.dpline.dao.entity.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class DeployConfigManager {

    private DeployConfigConverter deployConfigConverter;

    private JarFileServiceImpl jarFileServiceImpl;

    private AbstractDeployConfig abstractDeployConfig;

    private Logger logger = LoggerFactory.getLogger(DeployConfigManager.class);

    public DeployConfigManager(JarFileServiceImpl jarFileServiceImpl) {
        this.jarFileServiceImpl = jarFileServiceImpl;
        // 后续添加依赖的时候需要
        deployConfigConverter = new FlinkDeployConfigConverter();
    }

    public AbstractDeployConfig getDeployConfig(Job job) {
        logger.info("Task config is converting...");
        deployConfigConverter.convertToMotorDeployConfig(job);
        JarFile mainEffectJar = jarFileServiceImpl.getMapper().findMainEffectJar(job.getMainResourceId());
        if(Asserts.isNull(mainEffectJar)){
            logger.error("Main resource [{}] is null", job.getMainResourceId());
            return null;
        }
        mainJar(mainEffectJar);
        SourceConfig sourceConfig = JSONUtils.parseObject(job.getSourceContent(), SourceConfig.class);
        if (Asserts.isNull(sourceConfig)){
            return null;
        }
        List<SourceConfig.MainResourceDepend> jarDependList = sourceConfig.getJarDependList();
        jarDependList.forEach(jarDepend -> {
            // connector
            if (JarType.FunctionType.CONNECTOR.getValue().equals(jarDepend.getJarFunctionType())){
                List<JarFile> jarFiles = jarFileServiceImpl.getMapper().selectByMainResource(
                        jarDepend.getMainResourceId(),
                        job.getMotorVersionId());
                if(CollectionUtils.isEmpty(jarFiles)){
                    logger.error("Connector file [{}] is not find error.",jarDepend.getMainResourceId());
                    return;
                }
                connectorJarFiles(jarFiles);
                // udf
            } else if (JarType.FunctionType.UDF.getValue().equals(jarDepend.getJarFunctionType())){
                List<JarFile> jarFiles = jarFileServiceImpl.getMapper().selectByMainResource(
                        jarDepend.getMainResourceId(),
                        job.getMotorVersionId());
                if(CollectionUtils.isEmpty(jarFiles)){
                    logger.error("Udf file [{}] is not find error.",jarDepend.getMainResourceId());
                    return;
                }
                udfJarFiles(jarFiles);
            } else {
                // extend
                JarFile jarFile = jarFileServiceImpl.getMapper().findMainEffectJar(
                        jarDepend.getMainResourceId());
                if(Asserts.isNull(jarFile)){
                    logger.error("Extend file [{}] is not find error.",jarDepend.getMainResourceId());
                    return;
                }
                extendFiles(Arrays.asList(jarFile));
            }
        });
        logger.info("Task config is converted...");
        // 更新 main jar
        jarFileServiceImpl.getMapper().updateMainJarId(job.getId(), mainEffectJar.getId());
        return deployConfigConverter.getDeployConfig();
    }

    public DeployConfigManager mainJar(JarFile mainJarFile) {
        deployConfigConverter.mainJar(mainJarFile);
        return this;
    }

    public DeployConfigManager extendFiles(List<JarFile> extendJarFiles) {
        deployConfigConverter.extendJar(extendJarFiles);
        return this;
    }

    public DeployConfigManager connectorJarFiles(List<JarFile> connectorJarFiles) {
        deployConfigConverter.connectorJar(connectorJarFiles);
        return this;
    }

    public DeployConfigManager udfJarFiles(List<JarFile> udfJarFiles) {
        deployConfigConverter.udfJar(udfJarFiles);
        return this;
    }



}
