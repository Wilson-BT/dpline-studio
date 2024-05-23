package com.dpline.console.handler;

import com.dpline.common.enums.FileType;
import com.dpline.common.store.FsStore;
import com.dpline.common.util.FileUtils;
import com.dpline.common.util.TaskPathResolver;
import com.dpline.dao.domain.AbstractDeployConfig;
import com.dpline.dao.domain.FlinkDeployConfig;
import com.dpline.dao.domain.JarDepend;
import com.dpline.dao.entity.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 */
public class ApplicationDeployHandler implements DeployHandler {

    private final FsStore fsStore;
    private final AbstractDeployConfig deployConfig;
    private final static Logger logger = LoggerFactory.getLogger(ApplicationDeployHandler.class);

    public ApplicationDeployHandler(FsStore fsStore,AbstractDeployConfig deployConfig) {
        this.fsStore = fsStore;
        this.deployConfig = deployConfig;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean deploy(Job job) throws Exception {
        // 首先判断是不是Flink 类型
        // 2. 分别copy 到目标地址
        FlinkDeployConfig flinkDeployConfig = (FlinkDeployConfig) deployConfig;
        // delete remote path
        fsStore.delete(TaskPathResolver.getTaskRemoteDeployDir(job.getProjectId(),job.getId()),true);
        logger.info("Remote deploy dir [{}] is deleted.",TaskPathResolver.getTaskRemoteDeployDir(job.getProjectId(),job.getId()));
        copyFile(flinkDeployConfig,"Main");
        copyFile(flinkDeployConfig,"Extend");
        if(FileType.SQL_STREAM.getType().equals(job.getFileType())){
            copyFile(flinkDeployConfig,"Sql");
        }

        return true;
    }

    /**
     *
     * @param job
     */
    @Override
    public void clear(Job job) throws IOException {
        // 删除本地文件目录
        deleteLocalDir(TaskPathResolver.getTaskLocalDeployDir(job.getProjectId(),job.getId()));
        try {
            // 移除 task 部署目录
            fsStore.delete(TaskPathResolver.getTaskRemoteDeployDir(job.getProjectId(),job.getId()),true);
            // 移除ha
            fsStore.delete(TaskPathResolver.getTaskRemoteHaDir(job.getJobName()),true);
            // 移除所有checkpoint
            fsStore.delete(TaskPathResolver.getJobDefaultCheckPointDir(job.getProjectId(),job.getId()),true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteLocalDir(String path){
        try {
            File file = new File(path);
            if (file.exists()){
                FileUtils.deleteDirectory(file);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void copyFile(AbstractDeployConfig abstractDeployConfig,String fileTypeFlag) throws Exception {
        FlinkDeployConfig flinkDeployConfig = (FlinkDeployConfig) abstractDeployConfig;
        switch (fileTypeFlag){
            case "Extend":
                copyExtendFile(flinkDeployConfig);
                copyConnectorFile(flinkDeployConfig);
                copyUdfFile(flinkDeployConfig);
                break;
            case "Main":
                copyMainFile(flinkDeployConfig);
                break;
            case "Sql":
                copySqlFile(flinkDeployConfig);
                break;
            default:
                throw new RuntimeException(String.format("unsupport file type {}",fileTypeFlag));
        }
    }

    /**
     * copy extend file to remote
     * @param flinkDeployConfig
     * @throws Exception
     */
    private void copyExtendFile(FlinkDeployConfig flinkDeployConfig) throws Exception {
        Map<Long, JarDepend> extendIdPathMap = flinkDeployConfig.getExtendIdPathMap();
        String remoteStoreExtendPath = flinkDeployConfig.extendedRemoteFilePath();
        extendIdPathMap.forEach((key,value)->{
            String remotePath = remoteStoreExtendPath + "/" + value.getJarName();
            try {
                fsStore.copy(value.getJarPath(),remotePath,false,true);
                logger.info("Extend jar => [{}] has been copy success", value.getJarPath());
            } catch (IOException e) {
                logger.error("Extend jar => [{}] has been upload failed", value.getJarPath());
                throw new RuntimeException(e);
            }
        });
    }

    private void copyConnectorFile(FlinkDeployConfig flinkDeployConfig) throws Exception {
        Map<Long, JarDepend> extendIdPathMap = flinkDeployConfig.getConnectorIdPathMap();
        String remoteStoreExtendPath = flinkDeployConfig.extendedRemoteFilePath();
        extendIdPathMap.forEach((key,value)->{
            String remotePath = remoteStoreExtendPath + "/" + value.getJarName();
            try {
                fsStore.copy(value.getJarPath(),remotePath,false,true);
                logger.info("Connector jar => [{}] has been copy success", value.getJarPath());
            } catch (IOException e) {
                logger.error("Connector jar => [{}] has been upload failed", value.getJarPath());
                throw new RuntimeException(e);
            }
        });
    }

    private void copyMainFile(FlinkDeployConfig flinkDeployConfig) throws Exception {
        JarDepend mainJar = flinkDeployConfig.getMainJar();
        String remoteStoreMainPath = flinkDeployConfig.mainRemoteFilePath()+ "/" + mainJar.getJarName();
        fsStore.copy(mainJar.getJarPath(),remoteStoreMainPath,false,true);
        logger.info("Main jar => [{}] has been copy to [{}] success", mainJar.getJarPath(),remoteStoreMainPath);
    }
    private void copyUdfFile(FlinkDeployConfig flinkDeployConfig) throws Exception {
        Map<Long, JarDepend> udfPathMap = flinkDeployConfig.getUdfPathMap();
        String remoteStoreExtendPath = flinkDeployConfig.extendedRemoteFilePath();
        udfPathMap.forEach((key,value)->{
            String remotePath = remoteStoreExtendPath + "/" + value.getJarName();
            try {
                fsStore.copy(value.getJarPath(),remotePath,false,true);
                logger.info("Udf jar => [{}] has been copy success", value.getJarPath());
            } catch (IOException e) {
                logger.error("Udf jar => [{}] has been upload failed", value.getJarPath());
                throw new RuntimeException(e);
            }
        });
    }

    private void copySqlFile(FlinkDeployConfig flinkDeployConfig) throws Exception {
        byte[] bytes = flinkDeployConfig.getSqlText().getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        fsStore.upload(inputStream,flinkDeployConfig.sqlRemoteFilePath());
    }

}
