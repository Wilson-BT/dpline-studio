package com.handsome.alert.api;

import com.handsome.common.util.Asserts;
import com.handsome.common.util.JSONUtils;
import com.handsome.dao.entity.AlertInstance;
import com.handsome.dao.mapper.AlertInstanceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@Component
public class AlertManager {

    @Autowired
    AlertInstanceMapper alertInstanceMapper;

    @Autowired
    AlertConfig alertConfig;

    @Autowired
    AlertInstanceFactory alterInstanceFactory;

    private LinkedBlockingDeque<AlertEntity> blockingQueue;

    private ExecutorService executorService;

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
                    AlertToolInstance alertToolInstance = alterInstanceFactory.getInstance(alterEntity.getAlterType());
                    // 创建 AlertInfo
                    AlertInstance alertInstance = alertInstanceMapper.queryById(alterEntity.getAlterInstanceId());
                    Map<String, String> stringStringMap = JSONUtils.toMap(alertInstance.getInstanceParams());
                    String title = "实时任务预警";
                    String formatMess = "ID: [%s], TASK_NAME: [%s] turned from [%s] to [%s].";
                    AlertInfo alertBuildInfo = AlertInfo.builder()
                        .alertData(
                            new AlertData(
                                title,
                                String.format(formatMess, alterEntity.getAlterInstanceId(), alterEntity.getTaskName(), alterEntity.getTaskStatusBefore(), alterEntity.getTaskStatusCurrent()))
                        ).alertParams(stringStringMap).build();
                    AlertResult result = alertToolInstance.send(alertBuildInfo);
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
