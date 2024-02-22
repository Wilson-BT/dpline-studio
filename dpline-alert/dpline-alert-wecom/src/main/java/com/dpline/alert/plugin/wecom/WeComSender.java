package com.dpline.alert.plugin.wecom;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.dpline.alert.api.AlertResult;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class WeComSender {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(WeComSender.class);
    private static final String DEFAULT_CHARSET = "utf-8";
    private final String headerParams;
    private final Integer TIMEOUT = 10000;
    private String url;
    private HttpRequest httpRequest;

    public WeComSender(Map<String, String> paramsMap) {
        url = paramsMap.get(WeComAlertConstants.URL);
        headerParams = paramsMap.get(WeComAlertConstants.HEADER_PARAMS);
    }

    public AlertResult send(String title,String msg) {
        AlertResult alertResult = new AlertResult();
        // 生成http远程请求相关
        createHttpRequest(title,msg);
        try {
            String resp = httpRequest.execute().body();
            alertResult.setStatus("true");
            alertResult.setMessage(resp);
        } catch (Exception e) {
            log.error("send WeCom msg exception : {}", e.getMessage());
            alertResult.setStatus("false");
            alertResult.setMessage("send WeCom request alert fail.");
        }
        return alertResult;
    }

    private void createHttpRequest(String title,String msg) {
        // 企业微信采用post方式
        httpRequest = HttpRequest.post(url).timeout(TIMEOUT).charset(DEFAULT_CHARSET).contentType("application/json");

        setSimpleMsgInRequestBody(title,msg);
        // 设置header
        setHeader();
    }

    /**
     * set header params
     */
    private void setHeader() {

        if (StrUtil.isBlank(headerParams)) {
            return;
        }

        HashMap<String, String> map = JSONUtil.toBean(headerParams, HashMap.class);
        if (map.isEmpty()) {
            return;
        }
        // 设置header
        httpRequest.headerMap(map, true);
    }

    /**
     * set body params
     */
    private void setMsgInRequestBody(String msg) {
        JSONObject bodyJson = JSONUtil.parseObj(WeComAlertConstants.BODY_PARAMS);
        // 格式数据为企业微信markdown的数据内容
        StringBuffer bodyStrBuffer = new StringBuffer();
        // 通知信息内容
        if (StrUtil.isNotBlank(msg)) {
            JSONArray jsonArray = JSONUtil.parseArray(msg);
            bodyStrBuffer.append("Alert信息，请相关同学注意。\n");
            jsonArray.forEach(x -> {
                JSONObject object = (JSONObject) x;
                String markDownMsg = getMarkDownMsg(object);
                bodyStrBuffer.append(markDownMsg);
            });
        }
        bodyJson.putByPath(WeComAlertConstants.CONTENT_FIELD, bodyStrBuffer.toString());
        try {
            httpRequest.body(JSONUtil.toJsonStr(bodyJson));
        } catch (Exception e) {
            log.error("send WeCom alert msg  exception : {}", e.getMessage());
        }
    }

    public void setSimpleMsgInRequestBody(String title,String msg){
        JSONObject bodyJson = JSONUtil.parseObj(WeComAlertConstants.BODY_PARAMS);
        // 格式数据为企业微信markdown的数据内容
        StringBuilder bodyStrBuffer = new StringBuilder();
        // 通知信息内容
        if (StrUtil.isNotBlank(msg)) {
            bodyStrBuffer.append(title).append("\n");
            bodyStrBuffer.append(msg);
        }
        bodyJson.putByPath(WeComAlertConstants.CONTENT_FIELD, bodyStrBuffer.toString());
        try {
            httpRequest.body(JSONUtil.toJsonStr(bodyJson));
        } catch (Exception e) {
            log.error("send WeCom alert msg  exception : {}", e.getMessage());
        }
    }

    /**
     * 根据jsonObject生成对应企微markDown文本
     *
     * @param jsonObject
     * @return
     */
    private String getMarkDownMsg(JSONObject jsonObject) {
        if (null == jsonObject) {
            return null;
        }
        Set<String> keySet = jsonObject.keySet();
        StringBuffer stringBuffer = new StringBuffer();
        keySet.forEach(key -> {
            stringBuffer.append(">" + key + ":<font color=\"comment\">" + jsonObject.getStr(key) + "</font>\n");
        });
        return stringBuffer.toString();
    }
    @Override
    public String toString() {
        return "WeComSender{" +
                "headerParams='" + headerParams + '\'' +
                ", TIMEOUT=" + TIMEOUT +
                ", url='" + url + '\'' +
                ", httpRequest=" + httpRequest +
                '}';
    }
}
