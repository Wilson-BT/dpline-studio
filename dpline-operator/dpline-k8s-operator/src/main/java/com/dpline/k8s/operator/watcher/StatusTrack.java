package com.dpline.k8s.operator.watcher;

import com.dpline.common.enums.ExecStatus;
import com.dpline.k8s.operator.job.ClusterFlushEntity;
import com.dpline.operator.entity.ClusterEntity;

import java.util.Map;

public interface StatusTrack {


    /**
     * 状态触达
     * @return
     */
    Map<String,ExecStatus> remote(ClusterFlushEntity clusterFlushEntity);



}
