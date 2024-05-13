package com.dpline.common.params;


import com.dpline.common.Constants;
import com.dpline.common.enums.ResFsType;
import com.dpline.common.enums.ResUploadType;
import com.dpline.common.util.PropertyUtils;
import com.dpline.common.util.StringUtils;
import lombok.Data;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.dpline.common.Constants.BLACK;
import static com.dpline.common.Constants.DIVISION_STRING;


@Component
@Data
public class CommonProperties {

    private final static String DATA_BASEDIR_PATH = "data.basedir.path";

    private final static String RESOURCE_STORAGE_TYPE="resource.storage.type";

    private final static String TASK_LOG_PATH = "task.log.path";

    private final static String K8S_LOCAL_PATH_PREFIX = "k8s.local.path.prefix";

    private final static String YARN_OPERATOR_LISTEN_HOST = "operator.yarn.listen.host";

    private final static String YARN_OPERATOR_LISTEN_PORT = "operator.yarn.listen.port";

    private final static String K8S_OPERATOR_LISTEN_HOST = "operator.k8s.listen.host";

    private final static String K8S_OPERATOR_LISTEN_PORT="operator.k8s.listen.port";

    private final static String MONITOR_PROMETHEUS_URL="monitor.prometheus.url";


    public static String getDefaultFS() {
        return PropertyUtils.getProperty(Constants.FS_DEFAULTFS);
    }

    public static String getDataBasedirPath() {
        return pathDelimiterResolve(PropertyUtils.getProperty(DATA_BASEDIR_PATH,"/tmp/dpline"));
    }


    public static String getK8sLocalPathPrefix() {
        return PropertyUtils.getProperty(K8S_LOCAL_PATH_PREFIX,"/mnt/s3/flink");
    }


    public static String getYarnOperatorListenHost() {
        return PropertyUtils.getProperty(YARN_OPERATOR_LISTEN_HOST,"127.0.0.1");
    }
    public static String getK8sOperatorListenHost() {
        return PropertyUtils.getProperty(YARN_OPERATOR_LISTEN_HOST,"127.0.0.1");
    }

    public static Integer getYarnOperatorListenPort() {
        return PropertyUtils.getInt(YARN_OPERATOR_LISTEN_PORT,50055);
    }


    public static Integer getK8sOperatorListenPort() {
        return PropertyUtils.getInt(YARN_OPERATOR_LISTEN_PORT,50055);
    }

    public static String getMonitorPrometheusUrl(String defaultValue) {
        return PropertyUtils.getProperty(MONITOR_PROMETHEUS_URL,defaultValue);
    }

    public static String getTaskLogPath(){
        return pathDelimiterResolve(PropertyUtils.getProperty(TASK_LOG_PATH, getDataBasedirPath() + "/logs"));
    }


    public static ResUploadType getResourceStorageType(){
        return ResUploadType.valueOf(PropertyUtils.getUpperCaseString(RESOURCE_STORAGE_TYPE));
    }

    public static String getHadoopConfigDir(){
        return PropertyUtils.getProperty(Constants.HADOOP_CONF_DIR);
    }

    public static String pathDelimiterResolve(String path){
        if(StringUtils.isEmpty(path)){
            return BLACK;
        }
        if(path.endsWith(DIVISION_STRING)){
            path = path.substring(0,path.lastIndexOf(DIVISION_STRING));
        }
        return path;
    }


    public static boolean getResRemoteType(){
        String upperCaseString = PropertyUtils.getUpperCaseString(RESOURCE_STORAGE_TYPE);
        ResFsType resRemoteType = ResFsType.valueOf(upperCaseString);
        return resRemoteType == ResFsType.S3 || resRemoteType == ResFsType.LOCAL;
    }

    public static boolean loadKerberosConf(Configuration configuration) throws IOException {
        return loadKerberosConf(PropertyUtils.getProperty(Constants.JAVA_SECURITY_KRB5_CONF_PATH),
                PropertyUtils.getProperty(Constants.LOGIN_USER_KEY_TAB_USERNAME),
                PropertyUtils.getProperty(Constants.LOGIN_USER_KEY_TAB_PATH), configuration);
    }

    public static boolean loadKerberosConf(String javaSecurityKrb5Conf, String loginUserKeytabUsername, String loginUserKeytabPath, Configuration configuration) throws IOException {
        if (CommonProperties.getKerberosStartupState()) {
            System.setProperty(Constants.JAVA_SECURITY_KRB5_CONF, StringUtils.defaultIfBlank(javaSecurityKrb5Conf, PropertyUtils.getProperty(Constants.JAVA_SECURITY_KRB5_CONF_PATH)));
            configuration.set(Constants.HADOOP_SECURITY_AUTHENTICATION, Constants.KERBEROS);
            UserGroupInformation.setConfiguration(configuration);
            UserGroupInformation.loginUserFromKeytab(
                    StringUtils.defaultIfBlank(loginUserKeytabUsername, PropertyUtils.getProperty(Constants.LOGIN_USER_KEY_TAB_USERNAME)),
                    StringUtils.defaultIfBlank(loginUserKeytabPath, PropertyUtils.getProperty(Constants.LOGIN_USER_KEY_TAB_PATH)));
            return true;
        }
        return false;
    }

    public static boolean getKerberosStartupState() {
        ResUploadType resUploadType = CommonProperties.getResourceStorageType();
        Boolean kerberosStartupState = PropertyUtils.getBoolean(Constants.HADOOP_SECURITY_AUTHENTICATION_STARTUP_STATE, false);
        return resUploadType == ResUploadType.HDFS && kerberosStartupState;
    }

}
