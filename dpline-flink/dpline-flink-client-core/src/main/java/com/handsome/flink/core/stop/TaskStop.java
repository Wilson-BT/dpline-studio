package com.handsome.flink.core.stop;


import com.handsome.common.request.*;

/**
 * k8s application 模式提交任务
 */
public interface TaskStop {

    StopResponse stop(StopRequest stopRequest) throws Exception;

    TriggerResponse trigger(TriggerRequest triggerRequest) throws Exception;
}
