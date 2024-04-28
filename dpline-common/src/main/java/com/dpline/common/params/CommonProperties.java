package com.dpline.common.params;


import com.dpline.common.enums.ResFsType;
import com.dpline.common.util.PropertyUtils;
import com.dpline.common.util.StringUtils;
import lombok.Data;
import org.springframework.stereotype.Component;

import static com.dpline.common.Constants.BLACK;
import static com.dpline.common.Constants.DIVISION_STRING;


@Component
@Data
public class CommonProperties {

    private final static String DATA_BASEDIR_PATH = "data.basedir.path";

    private final static String RESOURCE_STORAGE_TYPE="resource.storage.type";

    private final static String TASK_LOG_PATH = "task.log.path";

    private final static String K8S_LOCAL_PATH_PREFIX = "k8s.local.path.prefix";

    private final static String OPERATOR_LISTEN_HOST = "operator.listen.host";

    private final static String OPERATOR_LISTEN_PORT="operator.listen.port";

    private final static String MONITOR_PROMETHEUS_URL="monitor.prometheus.url";

    // TODO 准备置为失效
    private final static String MONITOR_MOTOR_INGRESS_NAME="monitor.motor.ingress.name";


    public static String getDataBasedirPath() {
        return pathDelimiterResolve(PropertyUtils.getProperty(DATA_BASEDIR_PATH,"/tmp/dpline"));
    }


    public static String getK8sLocalPathPrefix() {
        return PropertyUtils.getProperty(K8S_LOCAL_PATH_PREFIX,"/mnt/s3/flink");
    }


    public static String getOperatorListenHost() {
        return PropertyUtils.getProperty(OPERATOR_LISTEN_HOST,"127.0.0.1");
    }


    public static Integer getOperatorListenPort() {
        return PropertyUtils.getInt(OPERATOR_LISTEN_PORT,50055);
    }

    public static String getMonitorPrometheusUrl(String defaultValue) {
        return PropertyUtils.getProperty(MONITOR_PROMETHEUS_URL,defaultValue);
    }

    public static String getTaskLogPath(){
        return pathDelimiterResolve(PropertyUtils.getProperty(TASK_LOG_PATH, getDataBasedirPath() + "/logs"));
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

}
