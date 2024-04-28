package com.dpline.alert.plugin.wecom;

import com.google.auto.service.AutoService;
import com.dpline.alert.api.AlertData;
import com.dpline.alert.api.AlertInfo;
import com.dpline.alert.api.AlertResult;
import com.dpline.alert.api.AlertToolInstance;

import java.util.Map;

@AutoService(AlertToolInstance.class)
public final class WeComAlertChannel extends AlertToolInstance {

    @Override
    public String name() {
        return "WECOM";
    }

    @Override
    public AlertResult send(AlertInfo alertInfo) {
        AlertData alertData = alertInfo.getAlertData();
        Map<String, String> paramsMap = alertInfo.getAlertParams();
        if (null == paramsMap) {
            return new AlertResult("false", "weCom params is null");
        }
        return new WeComSender(paramsMap).send(alertData.getTitle(),alertData.getContent());
    }
}