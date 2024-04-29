package com.dpline.k8s.operator.entry;

import io.fabric8.kubernetes.api.model.networking.v1beta1.HTTPIngressPath;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class IngressCustomConfig {

    Map<String, String> nginxRewriteMap;

    List<HTTPIngressPath> httpIngressPathList;

    String ingressHost;

    String ingressName;

}
