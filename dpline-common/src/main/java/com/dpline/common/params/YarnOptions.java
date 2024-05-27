package com.dpline.common.params;

import lombok.Data;

import java.util.List;

@Data
public class YarnOptions {

    /**
     * hadoop local home path
     */
    private String HadoopHome;

    /**
     * flink home path on hdfs
     */
    private List<String> flinkJarDirPath;


    private String flinkDistJarPath;

    public static Builder builder() {
        return new Builder();
    }
    public YarnOptions() {
    }

    public static class Builder {
        private String HadoopHome;
        private List<String> flinkJarDirPath;
        private String flinkDistJarPath;

        public Builder HadoopHome(String HadoopHome) {
            this.HadoopHome = HadoopHome;
            return this;
        }

        public Builder flinkJarDirPath(List<String> flinkJarDirPath) {
            this.flinkJarDirPath = flinkJarDirPath;
            return this;
        }
        public Builder flinkDistJarPath(String flinkDistJarPath) {
            this.flinkDistJarPath = flinkDistJarPath;
            return this;
        }
        public YarnOptions build() {
            YarnOptions yarnOptions = new YarnOptions();
            yarnOptions.setHadoopHome(HadoopHome);
            yarnOptions.setFlinkJarDirPath(flinkJarDirPath);
            yarnOptions.setFlinkDistJarPath(flinkDistJarPath);
            return yarnOptions;
        }
    }


}
