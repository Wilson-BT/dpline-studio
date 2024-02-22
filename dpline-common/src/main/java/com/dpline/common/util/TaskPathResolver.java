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

    public static final String REMOTE_MNT_PATH = "%s/%s";

    public static final String EXTENDED_FILE_PATH = "%s/extended";

    public static final String SQL_FILE_PATH = "%s/sql";

    private static final String LOCAL_FLINK_CLIENT = "%s/lib";

    private static final String LOCAL_FLINK_CONF = "%s/conf";

    private static final String LOCAL_FLINK_OPT = "%s/opt";

    private static final String LOCAL_FLINK_BIN_FLINK = "%s/bin/flink";

    private static final String LOCAL_PROJECT_PATH = "%s/lib";

    private final static String RUN_JAR_PREFIX = "local:///opt/flink/main/%s";

    public static final String SAVEPOINT_DIR_FORMAT = "%s%s/%s";


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

        if (flinkVersionHomePath.endsWith("/")) {
            flinkVersionHomePath = flinkVersionHomePath.substring(0, flinkVersionHomePath.lastIndexOf("/"));
        }
        return String.format(LOCAL_FLINK_CLIENT, flinkVersionHomePath);
    }
    public static String getLocalFlinkOptPath(String flinkVersionHomePath) {
        if (flinkVersionHomePath == null || flinkVersionHomePath.length() == 0) {
            return flinkVersionHomePath;
        }
        return String.format(LOCAL_FLINK_OPT, CommonProperties.pathDelimiterResolve(flinkVersionHomePath));
    }


    public static String getLocalFlinkBinFlink(String flinkVersionHomePath){
        return String.format(LOCAL_FLINK_BIN_FLINK,CommonProperties.pathDelimiterResolve(flinkVersionHomePath));
    }

    public static String getLocalFlinkConfPath(String flinkVersionHomePath){
        return String.format(LOCAL_FLINK_CONF,CommonProperties.pathDelimiterResolve(flinkVersionHomePath));
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
        return String.format("task/%s/%s", projectId, jobId);
    }

    public static String getTaskRemoteHaDir(String jobName){
        return String.format("ha/%s",jobName);
    }

    public static String getJobDefaultCheckPointDir(long projectId, long jobId, String runJobId) {
        return getJobDefaultCheckPointDir(projectId,jobId) + "/" + runJobId;
    }

    public static String getJobDefaultCheckPointDir(long projectId, long jobId) {
        return String.format("checkpoint/%s/%s", projectId, jobId);
    }

    public static String getJobDefaultSavePointDir(long projectId, long jobId,String runJobId) {
        return String.format("savepoint/%s/%s/%s", projectId, jobId, runJobId);
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

    public static String getNewRestUrlPath(String namespace,String ingressHost,String clusterId) {
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
    public static String getWebViewUrl(String namespace,String ingressHost,String clusterId,String runJobId) {
        if (StringUtils.isEmpty(clusterId)) {
            return "";
        }
        return String.format("http://%s/%s/%s/#/job/%s/overview", ingressHost, namespace, clusterId, runJobId);
    }

//    public static String getSessionJarRunPath(String nameSpace,String sessionName,String jarId){
//        String restUrlPath = TaskPathResolver.getNewRestUrlPath(nameSpace, sessionName);
//        return String.format("%s/jars/%s/run", restUrlPath,jarId);
//    }

//
//    public static String getRestUploadPath(String nameSpace,String clusterId){
//        return getNewRestUrlPath(nameSpace,clusterId) + "/jars/upload";
//    }
//
//    public static String getRestJarGetPath(String nameSpace,String clusterId){
//        return getNewRestUrlPath(nameSpace,clusterId) + "/jars";
//    }
//
//    public static String getRestJarDeletePath(String nameSpace,String clusterId,String jarId){
//        return String.format("{}/{}/jars",getNewRestUrlPath(nameSpace,clusterId),jarId);
//    }


    public static String getServiceName(String clusterId) {
        if (StringUtils.isEmpty(clusterId)) {
            return "";
        }
        return clusterId + "-rest";
    }

//    /**
//     * 如果是 application 模式，不管是Sql 还是 custom-code，
//     *
//     * @param localJarPath
//     * @return
//     */
//    public static String getAppModeRunJarPath(String localJarPath){
//        return String.format("local://opt/flink/main-file/%s",
//            localJarPath.substring(localJarPath.lastIndexOf("/") + 1));
//    }


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
        return String.format(MAIN_FILE_PATH, getTaskLocalDeployDir(projectId,jobId));
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
        if(StringUtils.isEmpty(path)){
            return BLACK;
        }
        if(path.endsWith(DIVISION_STRING)){
            path = path.substring(0,path.lastIndexOf("/"));
        }
        return path;
    }



    /**
     * @return 扩展文件路径
     */
    public static String extendedFilePath(long projectId, long jobId){
        return String.format(EXTENDED_FILE_PATH,
            getTaskLocalDeployDir(projectId,jobId));
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
        return String.format(SQL_FILE_PATH,
            getTaskLocalDeployDir(projectId,jobId));
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
