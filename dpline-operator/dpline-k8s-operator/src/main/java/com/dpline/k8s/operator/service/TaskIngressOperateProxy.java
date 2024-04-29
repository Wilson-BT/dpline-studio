package com.dpline.k8s.operator.service;

import com.dpline.common.util.ExceptionUtil;
import com.dpline.common.util.Version;
import com.dpline.k8s.operator.job.ClusterFlushEntity;
import com.dpline.k8s.operator.k8s.K8sClusterManager;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TaskIngressOperateProxy  implements IngressService{

    @Autowired
    TaskIngressV1Service taskIngressV1Service;

    @Autowired
    TaskIngressService taskIngressService;

    @Autowired
    K8sClusterManager k8sClusterManager;

    private Logger logger = LoggerFactory.getLogger(TaskIngressOperateProxy.class);

    @Override
    public void addIngressRule(ClusterFlushEntity clusterFlushEntity) {
        try {
            if(isV1Version(clusterFlushEntity)){
                logger.info("Use V1 ingress version to add ingress rule");
                taskIngressV1Service.addIngressRule(clusterFlushEntity);
                return;
            }
            logger.info("Use V1Beta1 ingress version to add ingress rule");
            taskIngressService.addIngressRule(clusterFlushEntity);
        }catch (Exception ex){
            logger.error(ExceptionUtil.exceptionToString(ex));
        }
    }

    @Override
    public boolean clearIngressRule(ClusterFlushEntity clusterFlushEntity) {
        try {
            if(isV1Version(clusterFlushEntity.getClusterEntityId())){
                logger.info("Use V1 ingress version to clear ingress rule");
                return taskIngressV1Service.clearIngressRule(clusterFlushEntity);
            }
            logger.info("Use V1Beta1 ingress version to clear ingress rule");
            return taskIngressService.clearIngressRule(clusterFlushEntity);
        }catch (Exception ex){
            logger.error(ExceptionUtil.exceptionToString(ex));
        }
        return false;
    }

    boolean isV1Version(ClusterFlushEntity clusterFlushEntity) throws Exception {
        return isV1Version(clusterFlushEntity.getClusterEntityId());
    }


    boolean isV1Version(Long clusterEntityId) {
        Optional<KubernetesClient> k8sClientInstance = k8sClusterManager.getK8sClientInstance(clusterEntityId);
        if(!k8sClientInstance.isPresent()){
            logger.error("Can`t find k8s client.");
            throw new RuntimeException("Can`t find k8s client.");
        }
        return ingressInNetworkingV1(k8sClientInstance.get());
    }

    public boolean ingressInNetworkingV1(KubernetesClient client) {
        // networking.k8s.io/v1/Ingress is available in K8s 1.19
        // See:
        // https://kubernetes.io/docs/reference/using-api/deprecation-guide/
        // https://kubernetes.io/blog/2021/07/14/upcoming-changes-in-kubernetes-1-22/
        String serverVersion =
            client.getKubernetesVersion().getMajor()
                + "."
                + client.getKubernetesVersion().getMinor();
        String targetVersion = "1.19";
        try {
            return Version.parse(serverVersion)
                .compareTo(Version.parse(targetVersion))
                >= 0;
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to parse Kubernetes server version: {}", serverVersion);
            return serverVersion.compareTo(targetVersion) >= 0;
        }
    }

}
