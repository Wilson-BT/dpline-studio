package com.dpline.operator.watcher;

import com.dpline.common.enums.ExecStatus;
import com.dpline.operator.job.ClusterFlushEntity;

import java.util.Map;

public interface StatusTrack {


    /**
     * 状态触达
     * @param clusterFlushEntity
     * @return
     */
    Map<String,ExecStatus> remote(ClusterFlushEntity clusterFlushEntity);



}
