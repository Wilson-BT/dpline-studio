package com.dpline.k8s.operator.entry;

import io.fabric8.kubernetes.api.model.networking.v1.HTTPIngressPath;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class IngressV1CustomConfig {

    Map<String, String> nginxRewriteMap;

    List<HTTPIngressPath> httpIngressPathList;

    String ingressHost;

    String ingressName;

}
