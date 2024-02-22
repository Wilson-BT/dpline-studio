package com.handsome;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.utils.Serialization;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

public class PodYamlCreateTest {

    public static final String FLINK_POD_MAIN_DRIECTORY = "/opt/flink/main";
    public static final String FLINK_POD_EXTENDED_DRIECTORY = "/opt/flink/lib/extended";

    public static final String FLINK_EXTENDED_DRIECTORY_NAME = "flink-extended-jars-hostpath";

    public static final String FLINK_MAIN_DRIECTORY_NAME = "flink-main-jars-hostpath";
    @Test
    public void createPodYaml() throws IOException {
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
        VolumeMount volumeMount1 = new VolumeMountBuilder()
                .withMountPath(FLINK_POD_MAIN_DRIECTORY)
                .withName(FLINK_MAIN_DRIECTORY_NAME)
                .build();
        VolumeMount volumeMount2 = new VolumeMountBuilder()
                .withMountPath(FLINK_POD_EXTENDED_DRIECTORY)
                .withName(FLINK_EXTENDED_DRIECTORY_NAME)
                .build();

        Volume volume1 = new VolumeBuilder()
                .withName(FLINK_MAIN_DRIECTORY_NAME)
                .withHostPath(new HostPathVolumeSourceBuilder()
                        .withPath("${USER_MAIN_JARS}")
                        .withType("Directory")
                        .build())
                .build();
        Volume volume2 = new VolumeBuilder()
                .withName(FLINK_EXTENDED_DRIECTORY_NAME)
                .withHostPath(new HostPathVolumeSourceBuilder()
                        .withPath("${USER_EXTENDED_JARS}")
                        .withType("Directory")
                        .build())
                .build();
        HashMap<String, String> nodeSelector = new HashMap<>();
        nodeSelector.put("k8s.wonhigh.cn/role","flink");
        Pod pod = new PodBuilder()
                .withNewMetadata()
                .withName("${FLINK_POD_NAME}")
                .withNamespace("flink")
                .endMetadata()
                .withNewSpec()
                .withServiceAccount("xxx-sa")
                .addToNodeSelector(nodeSelector)
                .addNewContainer()
                .withName("flink-main-container")
                .withImage("${IMAGE_NAME}")
                .withImagePullPolicy("Always")
                .withSecurityContext(securityContext)
                .withResources(resourceRequirements)
                .addToVolumeMounts(volumeMount1)
                .addToVolumeMounts(volumeMount2)
                .endContainer()
                .addToVolumes(volume1)
                .addToVolumes(volume2)
                .endSpec()
                .build();
        String podConfig = Serialization.asYaml(pod);
        System.out.println(podConfig);
    }

}
