package com.handsome;

import com.dpline.common.params.K8sClusterParams;
import com.dpline.common.util.JSONUtils;
import com.dpline.dao.entity.Cluster;
import com.dpline.operator.k8s.K8sClusterManager;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.junit.Test;

import java.util.Optional;


public class k8sTest {

    K8sClusterManager k8sClusterManager;
    @Test
    public void deleteIngress(){
        Cluster cluster = new Cluster();
        this.k8sClusterManager = new K8sClusterManager();
        String nameSpace = "ts-flink-prd";
        String kubePath = "/Users/wangchunshun/.kube/config";
        K8sClusterParams k8sNameSpace = new K8sClusterParams();
        k8sNameSpace.setNameSpace(nameSpace);
        k8sNameSpace.setKubePath(kubePath);
        cluster.setId(11111L);
        cluster.setClusterParams(JSONUtils.toJsonString(k8sNameSpace));
        this.k8sClusterManager.createK8sClient(cluster.getId(),k8sNameSpace, 1);
        String k8sClientKey = K8sClusterManager.getK8sClientKey(kubePath, nameSpace);
        Optional<KubernetesClient> k8sClientInstance = k8sClusterManager.getK8sClientInstance(cluster.getId());
        String clusterId = "tidb-doris-retail-sync-retail-mps";
        k8sClientInstance.map(k8sClient -> {
            final Deployment deployment =
                k8sClient
                    .apps()
                    .deployments()
                    .withName(clusterId)
                    .get();
            if (deployment != null) {
                k8sClusterManager.deleteDeploymentByClient(k8sClient,clusterId);
            }

            // configmaps 如果还存在的话，需要删除
            k8sClusterManager.deleteConfigMapsByClient(k8sClient,clusterId);
            return true;
        }).orElse(false);
    }



}
