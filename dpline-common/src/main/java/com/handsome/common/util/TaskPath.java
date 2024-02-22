package com.handsome.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.handsome.common.Constants.*;

/**
 * 任务路径
 */
public class TaskPath {

    private static final Logger logger = LoggerFactory.getLogger(TaskPath.class);

    /**
     * 每个任务下的家目录
     * 前缀地址/task/projectCode/taskCode
     */
    private static final String LOCAL_TASK_WORKSPACE = "%s/task/%s/%s";

    private static final String LOCAL_FLINK_CLIENT = "%s/lib";

    private static final String LOCAL_PROJECT_PATH = "%s/lib";

    private static final String LOCAL_PROJECT_LOG_PATH = "%s/log";

    public static final String DATA_BASEDIR = PropertyUtils.getProperty(DATA_BASEDIR_PATH, "/tmp/dpline");

    public static String getLocalFlinkLibPath(String flinkVersionHomePath) {
        if (flinkVersionHomePath == null || flinkVersionHomePath.length() == 0) {
            return flinkVersionHomePath;
        }

        if (flinkVersionHomePath.endsWith("/")) {
            flinkVersionHomePath = flinkVersionHomePath.substring(0, flinkVersionHomePath.lastIndexOf("/"));
        }
        return String.format(LOCAL_FLINK_CLIENT, flinkVersionHomePath);
    }

    /**
     * 获取local
     *
     * @return
     */
    public static String getLocalProjectHomeLibPath() {
        String appHome = System.getProperty(APP_HOME);
        if (StringUtils.isEmpty(appHome)) {
            logger.error("APP_HOME not find, please check your sys env.");
            throw new IllegalArgumentException("APP_HOME not find.");
        }
        return String.format(LOCAL_PROJECT_PATH, appHome);
    }


    /**
     * 获取local
     *
     * @return
     */
    public static String getLocalProjectHomeLogPath() {
        String appHome = System.getProperty(APP_HOME);
        if (StringUtils.isEmpty(appHome)) {
            logger.error("APP_HOME not find, please check your sys env.");
            throw new IllegalArgumentException("APP_HOME not find.");
        }
        return String.format(LOCAL_PROJECT_LOG_PATH, appHome);
    }


    public static String getTaskDeployDir(long projectCode, long taskId) {
        return String.format("%s/task/%s/%s", DATA_BASEDIR, projectCode, taskId);
    }

    public static String getDownloadFilename(String filename) {
        String fileName = String.format("%s/download/%s/%s", DATA_BASEDIR, DateUtils.getCurrentTime(YYYYMMDDHHMMSS), filename);

        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return fileName;
    }

    /**
     * get upload file absolute path and name
     *
     * @param filename file name
     * @return local file path
     */
    public static String getUploadFilename(String filename) {
        String fileName = String.format("%s/resources/%s", DATA_BASEDIR, filename);
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        return fileName;
    }

    public static String getRestUrlPath(String clusterId) {
        if (StringUtils.isEmpty(clusterId)) {
            return "";
        }
        return String.format("http://%s/%s", INGRESS_HOST, getServiceName(clusterId));
    }

    public static String getRestUploadPath(String clusterId){
        return getRestUrlPath(clusterId) + "/jars/upload";
    }

    public static String getRestJarGetPath(String clusterId){
        return getRestUrlPath(clusterId) + "/jars";
    }

    public static String getRestJarDeletePath(String clusterId,String jarId){
        return String.format("{}/{}/jars",getRestUrlPath(clusterId),jarId);
    }


    public static String getServiceName(String clusterId) {
        if (StringUtils.isEmpty(clusterId)) {
            return "";
        }
        return clusterId + "-rest";
    }

    /**
     * 如果是 application 模式，不管是Sql 还是 custom-code，
     *
     * @param localJarPath
     * @return
     */
    public static String getAppModeRunJarPath(String localJarPath){
        return String.format("local://opt/flink/main-file/%s",
            localJarPath.substring(localJarPath.lastIndexOf("/") + 1));
    }

}
