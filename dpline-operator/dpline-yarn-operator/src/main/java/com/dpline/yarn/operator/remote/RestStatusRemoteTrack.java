package com.dpline.yarn.operator.remote;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.dpline.common.enums.ExecStatus;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.JSONUtils;
import com.dpline.operator.common.TaskRestUrlStatusConvertor;
import com.dpline.operator.entity.SniffEvent;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.dpline.operator.common.TaskRestUrlStatusConvertor.REST_JOBS_OVERVIEWS;

public class RestStatusRemoteTrack {


    private static final int FLINK_CLIENT_TIMEOUT_SEC = 30000;

    private Logger logger = LoggerFactory.getLogger(RestStatusRemoteTrack.class);


    public Map<String,ExecStatus> parseAndUpdateFromRestUrlStatus(String body) {
        // 需要每个 task 的remote 结果
        HashMap<String, ExecStatus> jobIdExecStatusMap = new HashMap<>();
        ObjectNode jsonNodes = JSONUtils.parseObject(body);
        ArrayNode arrayNode = (ArrayNode) jsonNodes.get("jobs");
        if(Asserts.isNull(arrayNode)){
            return jobIdExecStatusMap;
        }
        arrayNode.forEach(
                jsonNode -> {
                    String taskStatus = jsonNode.get("state").asText();
                    // 先转换每个 状态值
                    Optional<TaskRestUrlStatusConvertor.RestRunStatus> convertStatus = TaskRestUrlStatusConvertor.RestRunStatus.of(taskStatus);
                    String runJobId = jsonNode.get("jid").asText();
                    if (!convertStatus.isPresent()) {
                        logger.error("{} is not be identified by code,please check your code.", taskStatus);
                        jobIdExecStatusMap.put(runJobId,ExecStatus.NONE);
                    } else {
                        jobIdExecStatusMap.put(runJobId,TaskRestUrlStatusConvertor.restStatusConvertToExec(convertStatus.get()));
                    }
                }
        );
        return jobIdExecStatusMap;
    }

    public Map<String, ExecStatus> restRemote(String url) {
        // 中间的是 ingressHost
        try {
            HttpResponse response = HttpRequest.get(String.format(REST_JOBS_OVERVIEWS,url))
                    .timeout(FLINK_CLIENT_TIMEOUT_SEC)
                    .charset(StandardCharsets.UTF_8)
                    .contentType("application/json").execute();
            // 如果不可达的话，需要看看不可达的时间戳，如果高于10s的话，就判断为失败
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                logger.warn("Rest-url [{}] is not remote for status [{}]", url, response.getStatus());
                return null;
            }
            return parseAndUpdateFromRestUrlStatus(response.body());
        } catch (Exception e) {
            logger.warn("Rest-url [{}] is not remote for error [{}]", url, e.toString());
        }
        return null;
    }

}
