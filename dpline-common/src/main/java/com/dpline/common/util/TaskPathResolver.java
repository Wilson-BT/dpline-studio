package com.dpline.common.util;

import com.dpline.common.Constants;
import com.dpline.common.params.CommonProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.dpline.common.Constants.*;

/**
 * 任务路径
 */
public class TaskPathResolver {

    private static final Logger logger = LoggerFactory.getLogger(TaskPathResolver.class);

    private static final String SQL_FILE_NAME = "dpline-flink-app.sql";

    public static final String MAIN_FILE_PATH = "%s/main";

    public static final String MAIN_FILE_FLAG = "main";

    public static final String REMOTE_MNT_PATH = "%s/%s";

    public static final String EXTENDED_FLAG = "extended";

    public static final String EXTENDED_FILE_PATH = "%s/extended";

    public static final String SQL_FILE_PATH = "%s/sql";

    public static final String SQL_FLAG = "sql";

    private static final String LOCAL_FLINK_CONF = "conf";

    private static final String LOCAL_FLINK_OPT = "opt";

    private static final String LOCAL_FLINK_BIN_FLINK = "bin/flink";

    private static final String LIB_FLAG = "lib";

    private final static String RUN_JAR_PREFIX = "local:///opt/flink/main/%s";

    public static final String SAVEPOINT_DIR_FORMAT = "%s%s";


    public static final String DATA_BASEDIR = pathDelimiterResolve(CommonProperties.getDataBasedirPath());

    public static final String K8S_MNT_PATH_PREFIX = pathDelimiterResolve(CommonProperties.getK8sLocalPathPrefix());

    public static String getAppHomePath(){
        return pathDelimiterResolve(PropertyUtils.getProperty(Constants.APP_HOME));
    }

    public static String getFlinkRunMainJarPath(String jarName){
        return String.format(RUN_JAR_PREFIX,jarName);
    }


    public static String getLocalFlinkLibPath(String flinkVersionHomePath) {
        if (flinkVersionHomePath == null || flinkVersionHomePath.length() == 0) {
            return flinkVersionHomePath;
        }
        return FileUtils.concatPath(pathDelimiterResolve(flinkVersionHomePath), LIB_FLAG);
    }
    public static String getLocalFlinkOptPath(String flinkVersionHomePath) {
        if (flinkVersionHomePath == null || flinkVersionHomePath.length() == 0) {
            return flinkVersionHomePath;
        }
        return FileUtils.concatPath(CommonProperties.pathDelimiterResolve(flinkVersionHomePath),LOCAL_FLINK_OPT);
    }


    public static String getLocalFlinkBinFlink(String flinkVersionHomePath){
        return FileUtils.concatPath(CommonProperties.pathDelimiterResolve(flinkVersionHomePath),LOCAL_FLINK_BIN_FLINK);
    }

    public static String getLocalFlinkConfPath(String flinkVersionHomePath){
        return FileUtils.concatPath(CommonProperties.pathDelimiterResolve(flinkVersionHomePath),LOCAL_FLINK_CONF);
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
        return FileUtils.concatPath(appHome,LIB_FLAG);
    }

    /**
     * 本地 临时目录
     *
     * @param projectId
     * @param jobId
     * @return
     */
    public static String getTaskLocalDeployDir(long projectId, long jobId) {
        return String.format("%s/task/%s/%s", DATA_BASEDIR, projectId, jobId);
    }

    /**
     * 远端 部署目录
     *
     * @param projectId
     * @param jobId
     * @return
     */
    public static String getTaskRemoteDeployDir(long projectId, long jobId) {
        return String.format("/dpline/task/%s/%s", projectId, jobId);
    }

    public static String getTaskRemoteHaDir(String jobName){
        return String.format("/dpline/ha/%s",jobName);
    }

    public static String getJobDefaultCheckPointDir(long projectId, long jobId, String runJobId) {
        return getJobDefaultCheckPointDir(projectId,jobId) + DIVISION_STRING + runJobId;
    }

    public static String getJobDefaultCheckPointDir(long projectId, long jobId) {
        return String.format("/dpline/checkpoint/%s/%s", projectId, jobId);
    }

    public static String getJobDefaultSavePointDir(long projectId, long jobId,String runJobId) {
        return String.format("/dpline/savepoint/%s/%s/%s", projectId, jobId, runJobId);
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

//    public static String getRestUrlPath(String clusterId) {
//        if (StringUtils.isEmpty(clusterId)) {
//            return "";
//        }
//        return String.format("http://%s/%s", CommonProperties.getMotorMonitorWebHost(), getServiceName(clusterId));
//    }

    public static String getRestUrlPath(String namespace,String ingressHost,String clusterId) {
        if (StringUtils.isEmpty(clusterId)) {
            return "";
        }
        return String.format("http://%s/%s/%s", ingressHost, namespace, clusterId);
    }

    private static String getK8sRestUrlPath(String namespace,String ingressHost,String clusterId) {
        if (StringUtils.isEmpty(clusterId)) {
            return "";
        }
        return String.format("http://%s/%s/%s", ingressHost, namespace,clusterId);
    }

    /**
     * http://flink.topsports.com.cn/ts-flink-prd/retail-business-log-sync-to-doris/#/job/5e52d0f989d7eabb30ee4876a359d832/overview
     * @param namespace
     * @param ingressHost
     * @param clusterId
     * @param runJobId
     * @return
     */
    public static String jobOverViewUrl(String namespace,String ingressHost,String clusterId,String runJobId) {
        if (StringUtils.isEmpty(clusterId)) {
            return "";
        }
        return String.format("http://%s/%s/%s/#/job/%s/overview", ingressHost, namespace, clusterId, runJobId);
    }




    public static String getServiceName(String clusterId) {
        if (StringUtils.isEmpty(clusterId)) {
            return "";
        }
        return clusterId + "-rest";
    }



    public static String getJobLogPath(String deployType, String jobId,String traceId) {
        return String.format("%s/%s/%s/%s.log", CommonProperties.getTaskLogPath(),
            jobId,
            deployType,
            traceId);
    }

    /**
     * @return main 文件的位置
     */
    public static String mainFilePath(long projectId, long jobId){
        return FileUtils.concatPath(getTaskLocalDeployDir(projectId,jobId),MAIN_FILE_FLAG);
    }

    public static String mainRemoteFilePath(long projectId, long jobId){
        return String.format(MAIN_FILE_PATH, getTaskRemoteDeployDir(projectId,jobId));
    }

    /**
     * 挂载到 宿主节点上的路径
     * @param projectId
     * @param jobId
     * @return
     */
    public static String mainRemoteMntLocalFilePath(long projectId, long jobId){
        return String.format(REMOTE_MNT_PATH, K8S_MNT_PATH_PREFIX,
            mainRemoteFilePath(projectId,jobId));
    }

    public static String pathDelimiterResolve(String path){
        return CommonProperties.pathDelimiterResolve(path);
    }

    /**
     * @return 扩展文件路径
     */
    public static String extendedFilePath(long projectId, long jobId){
        return FileUtils.concatPath(getTaskLocalDeployDir(projectId,jobId), EXTENDED_FLAG);
    }

    public static String extendedRemoteFilePath(long projectId, long jobId){
        return String.format(EXTENDED_FILE_PATH,
            getTaskRemoteDeployDir(projectId,jobId));
    }

    public static String extendedRemoteMntLocalFilePath(long projectId, long jobId){
        return String.format(REMOTE_MNT_PATH, K8S_MNT_PATH_PREFIX,
            extendedRemoteFilePath(projectId,jobId));
    }
    /**
     * @return sql 文件路径
     */
    public static String sqlFilePath(long projectId, long jobId){
        return FileUtils.concatPath(getTaskLocalDeployDir(projectId,jobId), SQL_FLAG);
    }

    /**
     * @return sql 文件路径
     */
    public static String sqlRemoteFilePath(long projectId, long jobId){
        return String.format(SQL_FILE_PATH,
            getTaskRemoteDeployDir(projectId,jobId));
    }

    /**
     * @return sql 文件绝对路径名
     */
    public static String sqlFileAbsoluteName(long projectId, long jobId){
        return sqlFilePath(projectId, jobId) + "/" + SQL_FILE_NAME;
    }
}
