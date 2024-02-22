package com.dpline.console.handler;

import com.dpline.dao.dto.JobDto;
import com.dpline.dao.entity.Job;

public interface DeployExecutor {

    String deploy(JobDto jobDto);

    void clear(Job job);
}
