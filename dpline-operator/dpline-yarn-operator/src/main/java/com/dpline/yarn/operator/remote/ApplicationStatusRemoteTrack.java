package com.dpline.yarn.operator.remote;

import com.dpline.common.enums.ExecStatus;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.JSONUtils;
import com.dpline.common.util.YarnUtil;
import com.dpline.operator.common.TaskRestUrlStatusConvertor;
import com.dpline.operator.entity.SniffEvent;
import com.dpline.yarn.operator.HadoopManager;
import com.dpline.yarn.operator.metric.YarnAppInfo;

import java.util.HashMap;
import java.util.Map;

public class ApplicationStatusRemoteTrack {

    /**
     *
     * only application mode
     * @param sniffEvent
     * @return
     */
    public ExecStatus remote(SniffEvent sniffEvent){
        String url = "ws/v1/cluster/apps/".concat(sniffEvent.getApplicationId());
        YarnAppInfo yarnAppInfo = HadoopManager.getHadoop(sniffEvent.getClusterId()).map(x -> {
            String result = YarnUtil.doRequest(url, YarnUtil.getYarnRestUrl(x.getConfiguration()));
            return JSONUtils.parseObject(result, YarnAppInfo.class);
        }).orElse(null);
        ExecStatus execStatus;
        // parse yarn app info to map
        if (Asserts.isNotNull(yarnAppInfo)) {
            execStatus = TaskRestUrlStatusConvertor.yarnStatusConvertToLocal(yarnAppInfo.getApp().getFinalStatus());
        } else {
            execStatus = ExecStatus.NONE;
        }
        return execStatus;
    }
}
