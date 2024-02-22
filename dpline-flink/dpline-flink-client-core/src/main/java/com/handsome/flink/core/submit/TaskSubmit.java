package com.handsome.flink.core.submit;


import com.handsome.common.request.SubmitRequest;
import com.handsome.common.request.SubmitResponse;

/**
 * k8s application 模式提交任务
 */
public interface TaskSubmit {

    SubmitResponse submit(SubmitRequest submitRequest) throws Exception;

}
