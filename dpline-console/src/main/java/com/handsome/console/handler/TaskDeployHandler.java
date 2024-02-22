package com.handsome.console.handler;

import com.handsome.common.enums.Flag;
import com.handsome.common.enums.ResFsType;
import com.handsome.common.util.*;
import com.handsome.dao.entity.FlinkRunTaskInstance;
import com.handsome.dao.entity.FlinkTaskTagLog;
import com.handsome.common.util.TaskPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;


public abstract class TaskDeployHandler implements DeployHandler {

    private static final Logger logger = LoggerFactory.getLogger(TaskDeployHandler.class);

    public static final String MAIN_FILE_KEY = "main-file";


    MinioUtils minio = MinioUtils.getInstance();


    public abstract boolean createLocalDirAndJars(String mainJarPath, String sqlText, String taskDeployDir);


    /**
     * 将本地文件目录下的所有文件 全部都提交到S3上
     *
     * @param localTaskDir
     * @param remoteTaskDir
     * @return
     */
    public boolean deployLocalDirAndJarsToS3(String localTaskDir, String remoteTaskDir, Flag isDeployed) throws IOException {
        // 将本地的所有文件都上传到 remoteTaskDir
        try {
            if (isDeployed == null || isDeployed.equals(Flag.YES)) {
                minio.delete(remoteTaskDir, true);
            }
            putAllFileToS3(new File(localTaskDir), remoteTaskDir);
            return true;
        } catch (Exception e) {
            logger.error("Put file from local path [{}] to S3 path [{}] failed.", localTaskDir, remoteTaskDir);
            return false;
        }
    }

    private void putAllFileToS3(File rootFile, String remoteTaskDir) throws IOException {
        File[] files = rootFile.listFiles();
        assert files != null;
        for (File file : files) {
            String taskDirElement = remoteTaskDir + "/" + file.getName();
            if (file.isDirectory()) {
                minio.mkdir(taskDirElement);
                putAllFileToS3(file, taskDirElement);
            } else {
                minio.copyLocalToMino(file.getAbsolutePath(), taskDirElement, false, true);
            }
        }
    }

    @Override
    public void clear(FlinkRunTaskInstance flinkRunTaskInstance) {
        deleteLocalDirExcludePodFile(flinkRunTaskInstance);
    }

    /**
     * 本地路径 task/project_code/taskId
     *
     * @param flinkTaskTagLog
     * @param flinkRunTaskInstance
     */
    public String deploy(FlinkTaskTagLog flinkTaskTagLog, FlinkRunTaskInstance flinkRunTaskInstance) throws URISyntaxException, IOException {
        String taskDeployDir = TaskPath.getTaskDeployDir(flinkTaskTagLog.getProjectCode(), flinkRunTaskInstance.getId()) + "/" + MAIN_FILE_KEY;
        FileUtils.deleteFile(taskDeployDir, ResFsType.LOCAL);
        logger.info("[{}] has been deleted.", taskDeployDir);
        boolean createLocalSuccess = createLocalDirAndJars(flinkTaskTagLog.getMainJarPath(), flinkTaskTagLog.getSqlText(), taskDeployDir);
        if (createLocalSuccess) {
            return pushFile(flinkRunTaskInstance, taskDeployDir);
        }
        logger.warn("Create local sql file failed or create pod file failed");
        return null;
    }



    /**
     * 如果是
     *
     * @param flinkRunTaskInstance
     */
    protected String pushFile(FlinkRunTaskInstance flinkRunTaskInstance, String taskDeployDir) throws IOException {
        String minoTaskDir = MinioUtils.getMinoTaskDir(String.valueOf(flinkRunTaskInstance.getProjectCode()),
            flinkRunTaskInstance.getFlinkTaskInstanceName());
        deployLocalDirAndJarsToS3(taskDeployDir, minoTaskDir, flinkRunTaskInstance.getDeployed());
        logger.info("put local file to s3 path [{}] has been success", minoTaskDir);
        return minoTaskDir;
    }

    /**
     * 删除掉所有数据，除了Pod文件
     *
     */
    public void deleteLocalDirExcludePodFile(FlinkRunTaskInstance flinkRunTaskInstance) {
        String taskLocalDeployDir = TaskPath.getTaskDeployDir(flinkRunTaskInstance.getProjectCode(), flinkRunTaskInstance.getId());
        File rootFile = new File(taskLocalDeployDir);
        for (File file : Objects.requireNonNull(rootFile.listFiles())) {
//            if (file.getName().equals(POD_FILE_NAME)) {
//                break;
//            }
            FileUtils.deleteFile(file.getAbsolutePath(), ResFsType.LOCAL);
        }
        logger.info("local dir [{}] has been clean.", taskLocalDeployDir);
    }

    public void deleteLocalDir(long projectCode, long taskId) {
        String taskLocalDeployDir = TaskPath.getTaskDeployDir(projectCode, taskId);
        File rootFile = new File(taskLocalDeployDir);
        if (rootFile.exists()) {
            FileUtils.deleteFile(rootFile.getAbsolutePath(), ResFsType.LOCAL);
        }
    }

}
