package com.handsome;

import com.dpline.common.Constants;
import com.dpline.common.params.CommonProperties;
import com.dpline.common.params.K8sClusterParams;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.JSONUtils;
import com.dpline.common.util.TaskPathResolver;
import com.dpline.dao.entity.Cluster;
import com.dpline.operator.entry.IngressV1CustomConfig;
import com.dpline.operator.entry.IngressV1RulePath;
import com.dpline.operator.k8s.K8sClusterManager;
import com.dpline.operator.service.TaskIngressOperateProxy;
import com.dpline.operator.service.TaskIngressService;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.networking.v1.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.base.PatchContext;
import io.fabric8.kubernetes.client.dsl.base.PatchType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class IngressV1OperatorTest {

    K8sClusterManager k8sClientManager;

    public static final String K8S_INGRESS_V1_VERSION = "networking.k8s.io/v1";

    public static final String NGINX_REWRITE_V1 = "nginx.ingress.kubernetes.io/rewrite-target";

    @Test
    public void preWork(){
        Cluster cluster = new Cluster();
        k8sClientManager = new K8sClusterManager();
        String nameSpace = "flink";
        String kubePath = "/Users/wangchunshun/.kube/config";
        K8sClusterParams k8sNameSpace = new K8sClusterParams();
        k8sNameSpace.setNameSpace(nameSpace);
        k8sNameSpace.setKubePath(kubePath);
        cluster.setClusterParams(JSONUtils.toJsonString(k8sNameSpace));
        k8sClientManager.createK8sClient(cluster.getId(),k8sNameSpace, 1);

    }

    @Test
    public void addIngressMap() {
        // 先创建k8s client
        K8sClusterManager k8sClientManager = new K8sClusterManager();
        String nameSpace = "flink";
        String kubePath = "/Users/wangchunshun/.kube/config";
        Cluster cluster = new Cluster();
        K8sClusterParams k8sNameSpace = new K8sClusterParams();
        k8sNameSpace.setNameSpace(nameSpace);
        k8sNameSpace.setKubePath(kubePath);
        cluster.setClusterParams(JSONUtils.toJsonString(k8sNameSpace));
        k8sClientManager.createK8sClient(cluster.getId(),k8sNameSpace, 1);

        TaskIngressService taskIngressService = new TaskIngressService();
        //String clusterId, Integer port, String nameSpace, String kubePath
//        taskIngressService.addIngressRule("flink-sync-database-retail-pos", nameSpace, kubePath);

    }

    @Test
    public void removeByPathTest() {
        K8sClusterManager k8sClientManager = new K8sClusterManager();
        String nameSpace = "flink";
        String kubePath = "/Users/wangchunshun/.kube/config";
        Cluster cluster = new Cluster();
        K8sClusterParams k8sNameSpace = new K8sClusterParams();
        k8sNameSpace.setNameSpace(nameSpace);
        k8sNameSpace.setKubePath(kubePath);
        cluster.setClusterParams(JSONUtils.toJsonString(k8sNameSpace));
        k8sClientManager.createK8sClient(cluster.getId(),k8sNameSpace, 1);
        Optional<KubernetesClient> k8sClientInstance = k8sClientManager.getK8sClientInstance(cluster.getId());
        k8sClientInstance.ifPresent(client -> {
            client.network()
                .ingresses()
                .withName(k8sNameSpace.getIngressName())
                .patch(PatchContext.of(PatchType.JSON), "[{\"op\": \"remove\", \"path\":\"/spec/rules/0/http/paths/1\"}]");
        });

    }

    @Test
    public void removeByNameTest() {
        K8sClusterManager k8sClusterManager = new K8sClusterManager();
        String nameSpace = "flink";
        String kubePath = "/Users/wangchunshun/.kube/config";
        K8sClusterParams k8sClusterParams = new K8sClusterParams();
        Cluster cluster = new Cluster();
        k8sClusterParams.setNameSpace(nameSpace);
        k8sClusterParams.setKubePath(kubePath);
        k8sClusterParams.setIngressName("");
        k8sClusterManager.createK8sClient(cluster.getId(),k8sClusterParams, 1);
        Optional<KubernetesClient> k8sClientInstance = k8sClusterManager.getK8sClientInstance(cluster.getId());
//        HTTPIngressPath httpIngressPath = new HTTPIngressPathBuilder()
//            .withPath("/flink-sync-database-retail-mps-rest/")
//            .withNewBackend()
//            .withServiceName("flink-sync-database-retail-mps-rest")
//            .withServicePort(new IntOrString(8081))
//            .endBackend()
//            .build();
        IngressV1RulePath ingressRulePath = new IngressV1RulePath(TaskPathResolver.getServiceName("test-0000000111"), nameSpace);
        HTTPIngressPath httpIngressPath = ingressRulePath.getHttpIngressPath();
        k8sClientInstance.ifPresent(client -> {
            client.network()
                .v1()
                .ingresses()
                .withName(k8sClusterParams.getIngressName())
                .edit(
                    i -> new IngressBuilder(i)
                        .editSpec()
                        .editLastRule()
                        .editHttp()
                        .removeFromPaths(httpIngressPath)
                        .endHttp()
                        .endRule()
                        .endSpec()
                        .build()
                );
        });
    }

    @Test
    public void TaskServiceTest(){
        TaskIngressService taskIngressService = new TaskIngressService();
        taskIngressService.setK8sClusterManager(new K8sClusterManager());
//        taskIngressService.addIngress("flink-sync-database-retail-pos","flink","/Users/wangchunshun/.kube/config");
//        taskIngressService.delIngress("flink-sync-database-retail-pos","flink","/Users/wangchunshun/.kube/config");
    }

    @Test
    public void createIngress(){
        Cluster cluster = new Cluster();
        this.k8sClientManager = new K8sClusterManager();
        String nameSpace = "flink";
        String kubePath = "/Users/wangchunshun/.kube/config";
        K8sClusterParams k8sNameSpace = new K8sClusterParams();
        k8sNameSpace.setNameSpace(nameSpace);
        k8sNameSpace.setKubePath(kubePath);
        k8sNameSpace.setIngressName("");
        cluster.setId(11111L);
        cluster.setClusterParams(JSONUtils.toJsonString(k8sNameSpace));
        this.k8sClientManager.createK8sClient(cluster.getId(),k8sNameSpace, 1);

        HashMap<String, String> nginxRewriteMap = new HashMap<>();
        ArrayList<HTTPIngressPath> httpIngressPathList = new ArrayList<>();
        IngressV1RulePath ingressRulePath = new IngressV1RulePath("test-0000000111",nameSpace);
        nginxRewriteMap.put(NGINX_REWRITE_V1, "/$2");
        httpIngressPathList.add(ingressRulePath.getHttpIngressPath());
        IngressV1CustomConfig ingressCustomConfig = new IngressV1CustomConfig();
        ingressCustomConfig.setNginxRewriteMap(nginxRewriteMap);
        ingressCustomConfig.setHttpIngressPathList(httpIngressPathList);
        String k8sClientKey = K8sClusterManager.getK8sClientKey(kubePath, nameSpace);
        Optional<KubernetesClient> k8sClientInstance = k8sClientManager.getK8sClientInstance(cluster.getId());

        k8sClientInstance.ifPresent(k8sClient->{
            Ingress ingress = k8sClient.network()
                .v1()
                .ingresses()
                .withName(k8sNameSpace.getIngressName())
                .get();
            if (Asserts.isNotNull(ingress)){
                ingress.getMetadata().setAnnotations(ingressCustomConfig.getNginxRewriteMap());
                List<IngressRule> rules = ingress.getSpec().getRules();
                ingress.getSpec().setIngressClassName("nginx");
                IngressRule ingressRule = rules.get(0);
                ingressRule.getHttp().setPaths(ingressCustomConfig.getHttpIngressPathList());
                k8sClient.network().v1().ingresses().replace(ingress);
                return;
            }
            // 不存在就新建
            ArrayList<IngressRule> ingressRules = new ArrayList<>();
            ingressRules.add(
                new IngressRule("*",
                    new HTTPIngressRuleValueBuilder()
                        .withPaths(ingressCustomConfig.getHttpIngressPathList())
                        .build()));
            ingress =
                new IngressBuilder()
                    .withApiVersion(K8S_INGRESS_V1_VERSION)
                    .withNewMetadata()
                    .withName(k8sNameSpace.getIngressName())
                    .withAnnotations(ingressCustomConfig.getNginxRewriteMap())
                    .endMetadata()
                    .withNewSpec()
                    .withIngressClassName("nginx")
                    .withRules(ingressRules)
                    .endSpec()
                    .build();
            k8sClient.network().v1().ingresses().create(ingress);
        });


    }

    @Test
    public void deleteIngress(){
        Cluster cluster = new Cluster();
        this.k8sClientManager = new K8sClusterManager();
        String nameSpace = "flink";
        String kubePath = "/Users/wangchunshun/.kube/config";
        K8sClusterParams k8sNameSpace = new K8sClusterParams();
        k8sNameSpace.setNameSpace(nameSpace);
        k8sNameSpace.setKubePath(kubePath);
        cluster.setId(11111L);
        cluster.setClusterParams(JSONUtils.toJsonString(k8sNameSpace));
        this.k8sClientManager.createK8sClient(cluster.getId(),k8sNameSpace, 1);

        HashMap<String, String> nginxRewriteMap = new HashMap<>();
        ArrayList<HTTPIngressPath> httpIngressPathList = new ArrayList<>();
        IngressV1RulePath ingressRulePath = new IngressV1RulePath("flink-sync-database-retail-mdm-rest",nameSpace);
        nginxRewriteMap.put(NGINX_REWRITE_V1,"/$2");
        httpIngressPathList.add(ingressRulePath.getHttpIngressPath());
        IngressV1CustomConfig ingressCustomConfig = new IngressV1CustomConfig();
        ingressCustomConfig.setNginxRewriteMap(nginxRewriteMap);
        ingressCustomConfig.setHttpIngressPathList(httpIngressPathList);
        String k8sClientKey = K8sClusterManager.getK8sClientKey(kubePath, nameSpace);
        Optional<KubernetesClient> k8sClientInstance = k8sClientManager.getK8sClientInstance(cluster.getId());
        String clusterId = "test-0000000111";
        k8sClientInstance.map(k8sClient -> {
            final Deployment deployment =
                k8sClient
                    .apps()
                    .deployments()
                    .withName(clusterId)
                    .get();
            if (deployment != null) {
                k8sClientManager.deleteDeploymentByClient(k8sClient,clusterId);
            }

            // configmaps 如果还存在的话，需要删除
            k8sClientManager.deleteConfigMapsByClient(k8sClient,clusterId);
//            try {
//                // 查询是否存在，存在就更新
//                Ingress ingress = k8sClient.network()
//                    .ingress()
//                    .withName(Constants.K8S_INGRESS_NAME)
//                    .get();
//                if (Asserts.isNull(ingress)){
//                    return true;
//                }
//                // 如果为空，直接删除
//                if(CollectionUtils.isEmpty(ingressCustomConfig.getHttpIngressPathList())){
//                    k8sClient.network().ingress().withName(Constants.K8S_INGRESS_NAME).delete();
//                    return true;
//                }
//                // 不为空，直接替换
//                ingress.getMetadata().setAnnotations(ingressCustomConfig.getNginxRewriteMap());
//                List<IngressRule> rules = ingress.getSpec().getRules();
//                IngressRule ingressRule = rules.get(0);
//                ingressRule.getHttp().setPaths(ingressCustomConfig.getHttpIngressPathList());
//                k8sClient.network().ingress().replace(ingress);
//                return true;
//            } catch (Exception e) {
//                return false;
//            }
            return true;
        }).orElse(false);
    }

    @Test
    public void ifV1Version(){
        Cluster cluster = new Cluster();
        this.k8sClientManager = new K8sClusterManager();
        String nameSpace = "flink";
        String kubePath = "/Users/wangchunshun/.kube/config_bak";
        K8sClusterParams k8sNameSpace = new K8sClusterParams();
        k8sNameSpace.setNameSpace(nameSpace);
        k8sNameSpace.setKubePath(kubePath);
        cluster.setId(11111L);
        cluster.setClusterParams(JSONUtils.toJsonString(k8sNameSpace));
        this.k8sClientManager.createK8sClient(cluster.getId(),k8sNameSpace, 1);
        TaskIngressOperateProxy taskIngressOperateProxy = new TaskIngressOperateProxy();
        boolean b = taskIngressOperateProxy.ingressInNetworkingV1(this.k8sClientManager.getK8sClientInstance(cluster.getId()).get());
        System.out.println(b);
    }


}
