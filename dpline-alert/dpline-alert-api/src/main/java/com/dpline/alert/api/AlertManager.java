package com.dpline.alert.api;

import cn.hutool.core.date.DateUtil;
import com.dpline.common.enums.AlertType;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.JSONUtils;
import com.dpline.dao.entity.AlertInstance;
import com.dpline.dao.entity.Project;
import com.dpline.dao.mapper.AlertInstanceMapper;
import com.dpline.dao.mapper.JobMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@Component
public class AlertManager {

    @Autowired
    AlertInstanceMapper alertInstanceMapper;

    @Autowired
    JobMapper jobMapper;

    @Autowired
    AlertConfig alertConfig;

    @Autowired
    AlertInstanceFactory alterInstanceFactory;

    private LinkedBlockingDeque<AlertEntity> blockingQueue;

    private ExecutorService executorService;

    private static String TITLE = "【实时任务预警】";
    private static String FORMAT_MESS = "【PROJECT_NAME】: %s\n" +
        "【JOB_ID】: %s\n" +
        "【TASK_NAME】: %s\n" +
        "【DATE_TIME】: %s\n" +
        "【STATUS_BEFORE】: %s\n" +
        "【STATUS_AFTER】: %s.";

    private Logger logger = LoggerFactory.getLogger(AlertManager.class);

    public AlertManager() {
    }

    public synchronized AlertManager init(){
        if (Asserts.isNull(blockingQueue)){
            blockingQueue = new LinkedBlockingDeque<>(alertConfig.getAlterQueueCapacity());
        }
        if(Asserts.isNull(executorService)){
            executorService = Executors.newFixedThreadPool(alertConfig.getAlterConsumeThreadNum());
            executorService.execute(new Sender());
        }

        return this;
    }

    public void accept(AlertEntity alterEntity) {
        try {
            // 如果不行的话，继续 offer
            if (!blockingQueue.offer(alterEntity, alertConfig.getAlterQueueOfferTimeout(), TimeUnit.MILLISECONDS)) {
                logger.error("Alert queue is offer failed for [{}]ms", alertConfig.getAlterQueueOfferTimeout());
            }
        } catch (InterruptedException e) {
            logger.error("Alert queue is offer failed for [{}]ms", alertConfig.getAlterQueueOfferTimeout());
            e.printStackTrace();
        }
    }

    public class Sender implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    AlertEntity alterEntity = blockingQueue.take();
                    // alert msg
                    AlertInstance alertInstance = alertInstanceMapper.queryById(alterEntity.getAlterInstanceId());
                    // project msg
                    Project project = jobMapper.selectProjectByJobId(alterEntity.getJobId());
                    if(alertInstance == null){
                        continue;
                    }
                    Optional<AlertType> alertTypeOptional = AlertType.of(alertInstance.getAlertType());
                    if(!alertTypeOptional.isPresent()){
                        continue;
                    }
                    AlertToolInstance alertToolInstance = alterInstanceFactory.getInstance(alertTypeOptional.get());
                    // 创建 AlertInfo
                    Map<String, String> stringStringMap = JSONUtils.toMap(alertInstance.getInstanceParams());
                    AlertInfo alertInfo = AlertInfo.builder()
                            .alertData(
                                new AlertData(
                                    TITLE,
                                    String.format(FORMAT_MESS, project.getProjectName(),
                                        alterEntity.getJobId(),
                                        alterEntity.getTaskName(),
                                        DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"),
                                        alterEntity.getTaskStatusBefore(),
                                        alterEntity.getTaskStatusCurrent()))
                            )
                            .alertParams(stringStringMap)
                            .build();
                    AlertResult result = alertToolInstance.send(alertInfo);
                    if (result.getStatus().equals("false")){
                        logger.error("Send message error for {}",result.getMessage());
                    }
                } catch (InterruptedException e) {
                    logger.error("Send message error for {}",e.toString());
                    e.printStackTrace();
                }
            }
        }
    }
}
