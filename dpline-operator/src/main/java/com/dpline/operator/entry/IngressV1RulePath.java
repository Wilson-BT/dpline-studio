package com.dpline.operator.entry;

import com.dpline.common.util.TaskPathResolver;
import io.fabric8.kubernetes.api.model.networking.v1.*;
import lombok.Data;


@Data
public class IngressV1RulePath {

    private String clusterId;
    /**
     * 端口号 默认为 8081
     */
    private Integer port;

    private String nameSpace;

    private String serviceName;

    public IngressV1RulePath(String clusterId, String nameSpace, Integer port) {
        this.clusterId = clusterId;
        this.nameSpace = nameSpace;
        this.serviceName = TaskPathResolver.getServiceName(this.clusterId);
        this.port = port;
    }

    public IngressV1RulePath(String clusterId, String nameSpace) {
        this(clusterId,nameSpace,8081);
    }

    public HTTPIngressPath getHttpIngressPath() {
        return new HTTPIngressPathBuilder()
            .withPath(getServiceNamePath())
            .withPathType("ImplementationSpecific")
            .withNewBackend()
            .withNewService()
            .withName(this.serviceName)
            .withNewPort()
            .withNumber(this.port)
            .endPort()
            .endService()
            .endBackend()
            .build();
    }

    public String getServiceNamePath() {
        return String.format("/%s/%s(/|$)(.*)", this.nameSpace, this.clusterId);
    }

    public String getClusterId() {
        return clusterId;
    }

    public Integer getPort() {
        return port;
    }

}
