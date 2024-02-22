//package com.handsome;
//
//import com.dpline.common.params.K8sClusterParams;
//import com.dpline.common.util.JSONUtils;
//import com.dpline.dao.entity.Cluster;
//import com.dpline.operator.k8s.K8sClientManager;
//import io.fabric8.kubernetes.client.KubernetesClient;
//import org.junit.Test;
//
//import java.util.Optional;
//
//public class ConfigmapOperatorTest {
//
//    K8sClientManager k8sClientManager;
//
//    @Test
//    public void preWork(){
//        Cluster cluster = new Cluster();
//        k8sClientManager = new K8sClientManager();
//        String nameSpace = "ts-flink-prd";
//        String kubePath = "/Users/wangchunshun/.kube/config";
//        K8sClusterParams k8sNameSpace = new K8sClusterParams();
//        k8sNameSpace.setNameSpace(nameSpace);
//        k8sNameSpace.setKubePath(kubePath);
//        cluster.setClusterParams(JSONUtils.toJsonString(k8sNameSpace));
//        k8sClientManager.createK8sClient(cluster, 1);
//    }
//
//    @Test
//    public void deleteConfigMapsByClientTest(){
//        String nameSpace = "ts-flink-prd";
//        String kubePath = "/Users/wangchunshun/.kube/config";
//        Optional<KubernetesClient> k8sClientInstance = k8sClientManager.getK8sClientInstance(K8sClientManager.getK8sClientKey(kubePath, nameSpace));
//        k8sClientInstance.ifPresent(client->{
//            k8sClientManager.deleteConfigMapsByClient(client,"flink-sync-database-retail-pos");
//        });
//
//    }
//}
