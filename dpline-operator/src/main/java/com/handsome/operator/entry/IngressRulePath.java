package com.handsome.operator.entry;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.networking.v1beta1.HTTPIngressPath;
import io.fabric8.kubernetes.api.model.networking.v1beta1.HTTPIngressPathBuilder;
import lombok.Data;

@Data
public class IngressRulePath {

    private String serviceName;
    /**
     * 端口号 默认为 8081
     */
    private Integer port;

    private HTTPIngressPath httpIngressPath;

    public IngressRulePath(String serviceName, Integer port) {
        this.serviceName = serviceName;
        this.port = port;
        this.httpIngressPath = new HTTPIngressPathBuilder()
            .withPath(getServiceNamePath())
            .withNewBackend()
            .withServiceName(serviceName)
            .withServicePort(new IntOrString(this.port))
            .endBackend()
            .build();
    }

    public HTTPIngressPath getHttpIngressPath() {
        return httpIngressPath;
    }

    public String getNginxReWrite(){
        return String.format("serviceName=%s rewrite=/;",this.serviceName);
    }

    public String getServiceNamePath() {
        return String.format("/%s/",this.serviceName);
    }

    public String getServiceName() {
        return serviceName;
    }

    public Integer getPort() {
        return port;
    }

}
