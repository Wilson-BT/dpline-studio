package com.dpline.common.request;

import com.dpline.common.enums.FileType;
import com.dpline.common.enums.RunModeType;
import com.dpline.common.enums.RunMotorType;
import com.dpline.common.params.FlinkHomeOptions;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FlinkRequest implements Request {

    RunMotorType runMotorType = RunMotorType.FLINK;

    FlinkHomeOptions flinkHomeOptions;

    RunModeType runModeType;

    FileType fileType;

    /**
     * extended jars
     */
    List<JarResource> extendedJarResources = new ArrayList<>();

}
