package com.dpline.flink.core.util;

import com.dpline.common.Constants;
import com.dpline.common.util.*;
import com.dpline.flink.core.stop.QueueStatus;
import com.dpline.flink.core.stop.TriggerResult;
import com.dpline.flink.core.stop.TriggerSavepointResponse;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class FlinkRestUtil {

    private final static Logger logger = LoggerFactory.getLogger(FlinkRestUtil.class);

    private static FlinkRestUtil flinkRestUtil = new FlinkRestUtil();

    public final static String CANCEL_URL_PATH = "%s/jobs/%s";

    public final static String CANCEL_WITH_SAVEPOINT_URL_PATH = "%s/jobs/%s/savepoints";

    private final ExecutorService executorService =
        new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 5,
            Runtime.getRuntime().availableProcessors() * 10,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024),
            ThreadUtils.threadFactory("TaskStopper"),
            new ThreadPoolExecutor.AbortPolicy());

    private FlinkRestUtil() {
    }

    /**
     * 保存一次 savepoint 之后再cancel
     *
     * @param savePointPath
     * @return
     */
    public String cancelOrSavePoint(String jobID, String httpPrefix, String savePointPath, boolean ifCancel) throws IOException {
        String url = String.format(CANCEL_WITH_SAVEPOINT_URL_PATH, httpPrefix, jobID);
        final AtomicBoolean succ = new AtomicBoolean(false);
        Map<String, String> bodyMap = new HashMap<>();
        if (ifCancel) {
            bodyMap.put("cancel-job","true");
        }

        if (StringUtils.isNotEmpty(savePointPath)){
            bodyMap.put("target-directory",savePointPath);
        }

        logger.info("Cancel or savepoint request, http-url [{}], body [{}]",url,bodyMap);
        // 提交 post 请求
        String content = "";
        content = HttpUtils.doStringBodyPost(url, JSONUtils.toJsonString(bodyMap));

        TriggerSavepointResponse triggerSavepointResponse = JSONUtils.parseObject(content,TriggerSavepointResponse.class);
        if(StringUtils.isBlank(content) || Asserts.isNull(triggerSavepointResponse) || StringUtils.isBlank(triggerSavepointResponse.getTriggerId())){
            return Constants.BLACK;
        }
        String triggerUrl = url + "/" + triggerSavepointResponse.getTriggerId();
        // 超时时间 10S 就退出
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            String savePointAddress = Constants.BLACK;
            while (!succ.get()){
                try {
                    String triggerContent = HttpUtils.doGet(new URIBuilder(triggerUrl).build());
                    TriggerResult triggerResult = JSONUtils.parseObject(triggerContent,TriggerResult.class);
                    if(Asserts.isNotNull(triggerResult) && triggerResult.getQueueStatus().getId().equals(QueueStatus.Id.COMPLETED)){
                        succ.set(true);
                        savePointAddress = triggerResult.getValue().getLocation();
                    }
                    if(!succ.get()){
                        Thread.sleep(1000L);
                    }
                } catch (Exception e) {
                    logger.error("Request to trigger failed", e);
                }
            }
            return savePointAddress;
        }, executorService);
        CompletableFuture<String> booleanCompletableFuture = CompletableFutureUtils.orTimeout(future, 10000L, TimeUnit.MILLISECONDS);
        try {
            return booleanCompletableFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            succ.set(false);
        }
        // 等待超时
        return Constants.BLACK;
    }

    public Boolean cancel(String jobID, String httpPrefix) throws URISyntaxException {
        String url = String.format(CANCEL_URL_PATH, httpPrefix, jobID);
        String content = HttpUtils.doPatch(new URI(url));
        return StringUtils.isNotBlank(content);
    }

    public static FlinkRestUtil getInstance(){
        return flinkRestUtil;
    }

}
