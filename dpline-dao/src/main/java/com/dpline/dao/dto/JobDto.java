package com.dpline.dao.dto;

import com.dpline.dao.entity.Cluster;
import com.dpline.dao.entity.FlinkVersion;
import com.dpline.dao.entity.Job;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;

@Data
public class JobDto extends Job {

    @Ignore
    private FlinkVersion flinkVersion;

    @Ignore
    private Cluster cluster;


}
