package com.handsome.flink.core.submit;

import com.handsome.common.request.Request;
import com.handsome.common.request.Response;
import com.handsome.common.request.SubmitRequest;
import com.handsome.common.request.SubmitResponse;
import com.handsome.common.util.Asserts;
import com.handsome.flink.core.AbstractTaskRunDynamicHandler;

import java.lang.reflect.Method;

public class TaskSubmitter extends AbstractTaskRunDynamicHandler implements TaskSubmit {

    /**
     * 任务提交
     *
     * @param submitRequest
     */
    public SubmitResponse doSubmit(SubmitRequest submitRequest) throws Exception {
        // 如果是k8s application 模式，获取到 classLoader ，然后本地的runAs方法
        return (SubmitResponse) runAsClassLoader(submitRequest);
//        return execFunc(Thread.currentThread().getContextClassLoader(), submitRequest);
    }

    /**
     * 执行方法
     */
    @Override
    public Response execFunc(ClassLoader targetClassLoader, Request remoteRequest) throws Exception {
        SubmitRequest submitRequest = null;
        if(remoteRequest instanceof SubmitRequest){
            submitRequest = (SubmitRequest) remoteRequest;
        }
        if(Asserts.isNull(submitRequest)){
            return null;
        }
        // 加载 submit 类
        String className = "";
        switch(submitRequest.getOtherOptions().getRunModeType()){
            case LOCAL:
                className = "com.handsome.flink.submit.LocalSubmitter";
                break;
            case K8S_SESSION:
                className = "com.handsome.flink.submit.K8sSessionSubmitter";
                break;
            case K8S_APPLICATION:
            default:
                className = "com.handsome.flink.submit.K8sApplicationSubmitter";
        }
        Class<?> clazz = targetClassLoader.loadClass(className);
//        Class<?> clazz = Class.forName(className);
        // 加载到内存中，并执行方法
        Method submitMethod = clazz.getDeclaredMethod("submit", SubmitRequest.class);
        submitMethod.setAccessible(true);
        return (SubmitResponse) submitMethod.invoke(clazz.newInstance(), submitRequest);
    }

    @Override
    public SubmitResponse submit(SubmitRequest submitRequest) throws Exception {
        return null;
    }
}
