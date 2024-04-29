package com.dpline.k8s.operator.k8s;

import com.dpline.common.enums.ResFsType;
import com.dpline.common.params.JobDefinitionOptions;
import com.dpline.common.params.K8sOptions;
import com.dpline.common.request.FlinkK8sRemoteSubmitRequest;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.FileUtils;
import com.dpline.common.util.TaskPathResolver;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.utils.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


public class PodYamlUtil {


    private static final String POD_FILE_NAME = "pod-template.yaml";

    public static final String FLINK_POD_MAIN_DRIECTORY = "/opt/flink/main";

    public static final String FLINK_POD_EXTENDED_DRIECTORY = "/opt/flink/lib/extended";

    public static final String FLINK_EXTENDED_DRIECTORY_NAME = "flink-extended-jars-hostpath";

    public static final String FLINK_MAIN_DRIECTORY_NAME = "flink-main-jars-hostpath";


    private static final Logger logger = LoggerFactory.getLogger(PodYamlUtil.class);

    public static String initPodTemplate(FlinkK8sRemoteSubmitRequest submitRequest) throws IOException {
        // read
        JobDefinitionOptions jobDefinitionOptions = submitRequest.getJobDefinitionOptions();
        return flushOut(
            TaskPathResolver.getTaskLocalDeployDir(
                jobDefinitionOptions.getProjectId(),
                jobDefinitionOptions.getJobId()),
                createPodYaml(submitRequest.getK8sOptions(),
                        jobDefinitionOptions));
    }

    public static String createPodYaml(K8sOptions k8sOptions, JobDefinitionOptions jobDefinitionOptions) throws IOException {
        SecurityContext securityContext = new SecurityContextBuilder()
                .withPrivileged(true)
                .withCapabilities(new CapabilitiesBuilder()
                        .addToAdd("SYS_ADMIN")
                        .addToAdd("NET_ADMIN")
                        .build())
                .build();
        ResourceRequirements resourceRequirements = new ResourceRequirementsBuilder()
                .addToRequests("ephemeral-storage", new Quantity("1024Mi"))
                .addToLimits("ephemeral-storage", new Quantity("1024Mi"))
                .build();
        VolumeMount volumeMountMain = new VolumeMountBuilder()
                .withMountPath(FLINK_POD_MAIN_DRIECTORY)
                .withName(FLINK_MAIN_DRIECTORY_NAME)
                .build();
        VolumeMount volumeMountExtend = new VolumeMountBuilder()
                .withMountPath(FLINK_POD_EXTENDED_DRIECTORY)
                .withName(FLINK_EXTENDED_DRIECTORY_NAME)
                .build();

        Volume volumeMain = new VolumeBuilder()
                .withName(FLINK_MAIN_DRIECTORY_NAME)
                .withHostPath(new HostPathVolumeSourceBuilder()
                        .withPath(TaskPathResolver.mainRemoteMntLocalFilePath(jobDefinitionOptions.getProjectId(), jobDefinitionOptions.getJobId()))
                        .withType("Directory")
                        .build())
                .build();
        Volume volumeExtend = new VolumeBuilder()
                .withName(FLINK_EXTENDED_DRIECTORY_NAME)
                .withHostPath(new HostPathVolumeSourceBuilder()
                        .withPath(TaskPathResolver.extendedRemoteMntLocalFilePath(jobDefinitionOptions.getProjectId(), jobDefinitionOptions.getJobId()))
                        .withType("Directory")
                        .build())
                .build();
        Pod pod = new PodBuilder()
                .withNewMetadata()
                .withName(jobDefinitionOptions.getJobName())
                .withNamespace(k8sOptions.getNameSpace())
                .endMetadata()
                .withNewSpec()
                .withServiceAccount(k8sOptions.getServiceAccount())
                .addToNodeSelector(k8sOptions.getSelectNode())
                .addNewContainer()
                .withName("flink-main-container")
                .withImage(k8sOptions.getImageAddress())
                .withImagePullPolicy("Always")
                .withSecurityContext(securityContext)
                .withResources(resourceRequirements)
                .addToVolumeMounts(volumeMountMain)
                .addToVolumeMounts(volumeMountExtend)
                .endContainer()
                .addToVolumes(volumeMain)
                .addToVolumes(volumeExtend)
                .endSpec()
                .build();
        return Serialization.asYaml(pod);
    }

    private static String flushOut(String path, String content) {
        String podFilePath = path + "/" + POD_FILE_NAME;
        FileWriter fileWriter = null;
        try {
            FileUtils.createDir(path, ResFsType.LOCAL);
            fileWriter = new FileWriter(podFilePath);
            fileWriter.write(content);
            fileWriter.flush();
            logger.info("Create pod file success, pod path {}", podFilePath);
        } catch (IOException exception) {
            FileUtils.deleteFile(podFilePath);
            logger.error("Create pod file error.",exception);
        } finally {
            if (Asserts.isNotNull(fileWriter)) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return podFilePath;
    }

}
