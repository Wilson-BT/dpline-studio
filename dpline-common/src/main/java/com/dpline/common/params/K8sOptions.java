package com.dpline.common.params;

import com.dpline.common.enums.ExposedType;
import lombok.Data;

import java.util.Map;

@Data
public class K8sOptions implements ClusterOptions {

    // 使用什么镜像
    private String imageAddress;

    private String nameSpace;

    private Map<String, String> selectNode;

    private String podFilePath;

    private ExposedType exposedType;

    private String clusterId;

    private String kubePath;

    private String sessionName;

    private String serviceAccount;

    /**
     * 用于session 模式 拼接 webUi，提交作业
     */
    private String ingressHost;

    /**
     *  暂时无用处
     */
    private String ingressName;

    public static Builder builder() {
        return new Builder();
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public static class Builder {

        private String imageAddress;

        private String nameSpace;

        private Map<String, String> selectNode;

//        private String podFilePath;

        private ExposedType exposedType = ExposedType.NODE_PORT;

        private String clusterId;

        private String kubePath;

        private String serviceAccount;

        private String ingressHost;

        private String ingressName;


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

        public Builder serviceAccount(String serviceAccount) {
            this.serviceAccount = serviceAccount;
            return this;
        }

        public Builder ingressHost(String ingressHost) {
            this.ingressHost = ingressHost;
            return this;
        }

        public Builder ingressName(String ingressName) {
            this.ingressName = ingressName;
            return this;
        }

        public K8sOptions build() {
            K8sOptions k8sOptions = new K8sOptions();
            k8sOptions.setClusterId(clusterId);
            k8sOptions.setExposedType(exposedType);
            k8sOptions.setImageAddress(imageAddress);
            k8sOptions.setNameSpace(nameSpace);
            k8sOptions.setSelectNode(selectNode);
            k8sOptions.setServiceAccount(serviceAccount);
            k8sOptions.setKubePath(kubePath);
            k8sOptions.setIngressName(ingressName);
            k8sOptions.setIngressHost(ingressHost);
            return k8sOptions;
        }

    }

}
