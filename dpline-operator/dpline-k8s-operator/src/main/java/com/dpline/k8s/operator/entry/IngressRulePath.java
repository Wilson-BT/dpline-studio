package com.dpline.k8s.operator.entry;

import com.dpline.common.util.TaskPathResolver;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.networking.v1beta1.HTTPIngressPath;
import io.fabric8.kubernetes.api.model.networking.v1beta1.HTTPIngressPathBuilder;
import lombok.Data;

@Data
public class IngressRulePath {

    private String clusterId;
    /**
     * 端口号 默认为 8081
     */
    private Integer port;

    private String serviceName;

    private String nameSpace;

    public IngressRulePath(String clusterId, String nameSpace, Integer port) {
        this.clusterId = clusterId;
        this.nameSpace = nameSpace;
        this.serviceName = TaskPathResolver.getServiceName(this.clusterId);
        this.port = port;
    }

    public IngressRulePath(String nameSpace,String clusterId) {
        this(clusterId,nameSpace,8081);
    }

    public HTTPIngressPath getHttpIngressPath() {
        return  new HTTPIngressPathBuilder()
            .withPath(getServiceNamePath())
            .withNewBackend()
            .withServiceName(this.serviceName)
            .withServicePort(new IntOrString(this.port))
            .endBackend()
            .build();
    }

    public String getNginxReWrite(){
        return String.format("serviceName=%s rewrite=/;\n", this.serviceName);
    }

    public String getServiceNamePath() {
        return String.format("/%s/%s/",this.nameSpace, this.clusterId);
    }

    public String getServiceName() {
        return serviceName;
    }

    public Integer getPort() {
        return port;
    }

}
