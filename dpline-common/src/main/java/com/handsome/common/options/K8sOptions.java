package com.handsome.common.options;

import com.handsome.common.enums.ExposedType;
import lombok.Data;

import java.util.Map;

@Data
public class K8sOptions {

    // 使用什么镜像
    private String imageAddress;

    private String nameSpace;

    private Map<String, String> selectNode;

    private String serviceAccount;

    private String podFilePath;

    private ExposedType exposedType;

    private String clusterId;

    private String kubePath;

    private String sessionName;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String imageAddress;

        private String nameSpace;

        private Map<String, String> selectNode;

        private String podFilePath;

        private ExposedType exposedType;

        private String clusterId;

        private String kubePath;

        private String serviceAccount;

        private String sessionName;

        public Builder imageAddress(String imageAddress) {
            this.imageAddress = imageAddress;
            return this;
        }

        public Builder nameSpace(String nameSpace) {
            this.nameSpace = nameSpace;
            return this;
        }

        public Builder selectNode(Map<String, String> selectNode) {
            this.selectNode = selectNode;
            return this;
        }

        public Builder podFilePath(String podFilePath) {
            this.podFilePath = podFilePath;
            return this;
        }

        public Builder exposedType(ExposedType exposedType) {
            this.exposedType = exposedType;
            return this;
        }

        public Builder clusterId(String clusterId) {
            this.clusterId = clusterId;
            return this;
        }

        public Builder kubePath(String kubePath) {
            this.kubePath = kubePath;
            return this;
        }

        public Builder serviceAccount(String serviceAccount){
            this.serviceAccount = serviceAccount;
            return this;
        }

        public Builder sessionName(String sessionName){
            this.sessionName = sessionName;
            return this;
        }

        public K8sOptions build() {
            K8sOptions k8sOptions = new K8sOptions();
            k8sOptions.setClusterId(clusterId);
            k8sOptions.setExposedType(exposedType);
            k8sOptions.setImageAddress(imageAddress);
            k8sOptions.setNameSpace(nameSpace);
            k8sOptions.setSelectNode(selectNode);
            k8sOptions.setKubePath(kubePath);
            k8sOptions.setServiceAccount(serviceAccount);
            k8sOptions.setPodFilePath(podFilePath);
            k8sOptions.setSessionName(sessionName);
            return k8sOptions;
        }

    }

}
