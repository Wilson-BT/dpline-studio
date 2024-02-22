package com.dpline.common.params;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class K8sClusterParams implements ClusterParams {

    /**
     * 命名空间
     */
    private String nameSpace;

    /**
     * kube config path
     */
    private String kubePath;

    /**
     * service account
     */
    private String serviceAccount;

    /**
     * ingress host
     */
    private String ingressHost;

    /**
     * ingress name to distinction distinguish cluster between different nodes
     */
    private String ingressName;

    /**
     * node selector
     */
    private List<Map<String, String>> extraParam = new ArrayList<>();

}
