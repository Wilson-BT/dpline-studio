package com.handsome.common;

import com.handsome.common.util.Asserts;
import com.handsome.common.util.EncryptionUtils;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class K8sClientManager {

    private static final Logger logger = LoggerFactory.getLogger(K8sClientManager.class);

    // create k8s client
    private static final ConcurrentHashMap<String, KubernetesClient> k8sClientMap = new ConcurrentHashMap<>();

    /**
     * create k8s client，
     * exist -> return
     * not exist -> put and return
     * @return
     */
    public static String getK8sClientKey(String kubeConfigPath,String nameSpace){
        return EncryptionUtils.getMd5(kubeConfigPath + nameSpace);
    }

    /**
     * unique key -> kubeConfigPath + namespace
     *
     * @param kubeConfigPath
     * @param nameSpace
     * @return
     */
    public static KubernetesClient createK8sClient(String kubeConfigPath,String nameSpace) {
        String md5Key = getK8sClientKey(kubeConfigPath, nameSpace);
        File configFile = new File(kubeConfigPath);
        try {
            Config configYaml = Config.fromKubeconfig(String.join("\n", Files.readAllLines(configFile.toPath())));
            configYaml.setTrustCerts(true);
            configYaml.setNamespace(nameSpace);
            KubernetesClient kubernetesClient =  new DefaultKubernetesClient(configYaml);
            k8sClientMap.computeIfAbsent(md5Key,(v) -> kubernetesClient);
            return kubernetesClient;
        } catch (IOException e) {
            logger.error("K8s client for [{}] create error",kubeConfigPath);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Delete k8s client
     *
     * @param kubeConfigPath
     * @return
     */
    public static void deleteClientIfExist(String kubeConfigPath,String nameSpace) {
        String md5 = EncryptionUtils.getMd5(kubeConfigPath + nameSpace);
        KubernetesClient kubernetesClient = k8sClientMap.get(md5);
        if(Asserts.isNull(kubernetesClient)){
            return;
        }
        k8sClientMap.remove(md5);
        kubernetesClient.close();
    }

    /**
     * Get k8s client by name or path
     *
     * @param kubePath kubeConfig 路径
     * @return
     */
    public static Optional<KubernetesClient> getK8sClientInstance(String kubePath,String nameSpace) {
        String md5 = EncryptionUtils.getMd5(kubePath + nameSpace);
        return Optional.ofNullable(k8sClientMap.get(md5));
    }
}
