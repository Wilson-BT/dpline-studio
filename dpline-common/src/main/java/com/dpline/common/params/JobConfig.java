package com.dpline.common.params;

import lombok.Data;
import java.io.Serializable;

@Data
public class JobConfig implements Serializable {

    /**
     * 运行模式
     */
    private String runMode;

    /**
     * 运行资源/运行配置设置
     */
    private RuntimeOptions runtimeOptions = new RuntimeOptions();

    /**
     * 运行 引擎版本设置
     */
    private MotorVersion motorVersion = new MotorVersion();

    /**
     * 运行集群设置
     */
    private RunClusterInfo runClusterInfo = new RunClusterInfo();

    /**
     * 运行镜像设置
     */
    private RunImageInfo runImageInfo = new RunImageInfo();

    /**
     * flink 的配置
     */
    private String flinkYaml;


    @Data
    public static class MotorVersion implements Serializable {

        /**
         * flink real version
         */
        private String motorRealVersion;

        private String motorPath;
        /**
         * flink Id
         */
        private Long motorId;
    }

    @Data
    public static class RunClusterInfo implements Serializable{

        /**
         * 集群类型
         */
        private String clusterType;

        /**
         * 集群 ID
         */
        private Long clusterId;

        /**
         * 集群 名称
         */
        private String clusterName;
    }

    @Data
    public static class RunImageInfo implements Serializable{

        private Long imageId;

        private String shortName;

        private String imageName;
    }
}
