package com.dpline.operator.processor;

import com.dpline.common.enums.ExecStatus;
import com.dpline.operator.entity.ClusterEntity;
import com.dpline.operator.entity.SniffEvent;

import java.util.Map;

public interface StatusTrack {


    /**
     * @return
     */
    Map<String,ExecStatus> restRemote(String url);

    ExecStatus applicationRemote(SniffEvent sniffEvent);



}
