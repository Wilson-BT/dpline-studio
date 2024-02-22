package com.handsome.flink.core.stop;

import com.handsome.common.enums.ResponseStatus;
import com.handsome.common.request.*;
import com.handsome.common.util.HttpUtils;
import com.handsome.common.util.StringUtils;
import com.handsome.common.util.TaskPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class TaskStopper {

    private final static Logger logger = LoggerFactory.getLogger(TaskStopper.class);

    public final static String CANCEL_URL_PATH = "%s/jobs/%s";

    public final static String CANCEL_WITH_SAVEPOINT_URL_PATH = "%s/jobs/%s/savepoints";

    public StopResponse stop(StopRequest stopRequest) throws IOException, URISyntaxException {
        String responseContent = "";
        String jobId = stopRequest.getJobId();
        String restUrlPath = TaskPath.getRestUrlPath(stopRequest.getClusterId());
        if (stopRequest.getWithSavePointAddress()){
            responseContent = cancelWithSavePoint(jobId, restUrlPath, stopRequest.getSavePointAddress());

        } else {
            responseContent = cancel(jobId, restUrlPath);
        }
        if(StringUtils.isNotEmpty(responseContent)){
            logger.info("Cluster:[{}], Task: [{}] has been stopped success. ResponseContent:[{}]",
                stopRequest.getClusterId(),
                stopRequest.getTaskId(),
                responseContent);
            return new StopResponse(stopRequest.getClusterId(),
                jobId,
                ResponseStatus.SUCCESS,
                stopRequest.getSavePointAddress());
        }
        logger.error("Cluster:[{}], Task: [{}] has been stopped failed. ResponseContent:[{}].",
            stopRequest.getClusterId(),
            stopRequest.getTaskId(),
            responseContent
        );
        return new StopResponse(stopRequest.getClusterId(),
            jobId,
            ResponseStatus.FAIL,
            stopRequest.getSavePointAddress());
    };

    /**
     * 直接cancel
     *
     * @return
     */
    public String cancel(String jobID, String httpPrefix) throws URISyntaxException {
        String url = String.format(CANCEL_URL_PATH, httpPrefix, jobID);
        return HttpUtils.doPatch(new URI(url));
    }

    /**
     * 保存一次 savepoint 之后再cancel
     *
     * @param savePointPath
     * @return
     */
    private String cancelOrSavePoint(String jobID, String httpPrefix, String savePointPath, boolean ifCancel) throws IOException {
        String url = String.format(CANCEL_WITH_SAVEPOINT_URL_PATH, httpPrefix, jobID);

        Map<String, String> bodyMap = new HashMap<>();
        // 如果停止的话
        if (ifCancel) {
            bodyMap.put("cancel-job","true");
        }

        if (StringUtils.isNotEmpty(savePointPath)){
            bodyMap.put("target-directory",savePointPath);
        }
        logger.info("CancelOrSavePoint request, http-url [{}], body [{}]",url,bodyMap);
        return HttpUtils.doPost(url,bodyMap,null);
    }

    /**
     * @return
     */
    public TriggerResponse triggerSavePoint(TriggerRequest triggerRequest) throws IOException {
        String responseContent = "";
        String jobId = triggerRequest.getJobId();
        String restUrlPath = TaskPath.getRestUrlPath(triggerRequest.getClusterId());
        // 使用什么服务
        responseContent = triggerWithSavePoint(jobId, restUrlPath, triggerRequest.getSavePointAddress());
        if(StringUtils.isNotEmpty(responseContent)){
            logger.info("Cluster:[{}], Task: [{}] saved to Path [{}] success. ResponseContent:[{}]",
                triggerRequest.getClusterId(),
                triggerRequest.getTaskId(),
                triggerRequest.getSavePointAddress(),
                responseContent);
            return new TriggerResponse(triggerRequest.getTaskId(),
                triggerRequest.getSavePointAddress(),
                ResponseStatus.SUCCESS);
        }
        logger.error("Cluster:[{}], Task: [{}] saved to Path [{}] failed. ResponseContent:[{}].",
            triggerRequest.getClusterId(),
            triggerRequest.getTaskId(),
            triggerRequest.getSavePointAddress(),
            responseContent
        );
        return new TriggerResponse(
            triggerRequest.getTaskId(),
            triggerRequest.getSavePointAddress(),
            ResponseStatus.FAIL);
    }

    /**
     * @param jobID
     * @param savePointPath
     * @return
     */
    public String cancelWithSavePoint(String jobID, String httpPrefix,String savePointPath) throws IOException {
        return cancelOrSavePoint(jobID,httpPrefix,savePointPath,true);
    }



    public String triggerWithSavePoint(String jobID, String httpPrefix,String savePointPath) throws IOException {
        return cancelOrSavePoint(jobID,httpPrefix,savePointPath,false);
    }



//    /**
//     *
//     * @param targetClassLoader
//     * @param remoteRequest
//     * @return
//     * @throws Exception
//     */
//    @Override
//    public Response execFunc(ClassLoader targetClassLoader, Request remoteRequest) throws Exception {
//        StopRequest stopRequest = null;
//        if(remoteRequest instanceof StopRequest){
//            stopRequest = (StopRequest) remoteRequest;
//        }
//        if(Asserts.isNull(stopRequest)){
//            return null;
//        }
//        // 加载 submit 类
//        String className = "";
//        switch(stopRequest.getRunModeType()){
//            case LOCAL:
//                className = "com.handsome.flink.stop.LocalStopper";
//                break;
//            case K8S_SESSION:
//                className = "com.handsome.flink.stop.K8sSessionStopper";
//                break;
//            case K8S_APPLICATION:
//            default:
//                className = "com.handsome.flink.stop.K8sApplicationStopper";
//        }
//        Class<?> clazz = targetClassLoader.loadClass(className);
////        Class<?> clazz = Class.forName(className);
//        // 加载到内存中，并执行方法
//        Method submitMethod = clazz.getDeclaredMethod("stop", StopRequest.class);
//        submitMethod.setAccessible(true);
//        return (SubmitResponse) submitMethod.invoke(clazz.newInstance(), stopRequest);
//    }
}
