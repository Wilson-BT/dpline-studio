package com.handsome.operator.job;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.handsome.common.enums.ExecStatus;
import com.handsome.common.enums.RunModeType;
import com.handsome.common.util.Asserts;
import com.handsome.common.util.JSONUtils;
import com.handsome.dao.entity.FlinkRunTaskInstance;
import com.handsome.dao.entity.K8sNameSpace;
import com.handsome.dao.mapper.K8sNameSpaceMapper;
import com.handsome.operator.config.WatcherConfig;
import com.handsome.operator.service.*;
import com.handsome.operator.k8s.K8sClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
public class TaskStatusWatchManager {

    private static final Logger logger = LoggerFactory.getLogger(TaskStatusWatchManager.class);

    @Autowired
    TaskRemoteService taskRemoteService;

    @Autowired
    TaskStatusService taskStatusService;

    @Autowired
    TaskClusterMapService taskClusterMapService;

    @Autowired
    WatcherConfig watcherConfig;

    @Autowired
    TaskIngressService taskIngressService;

    @Autowired
    LinkedBlockingDeque<ClusterFlushEntity> blockingQueue;

    @Autowired
    K8sNameSpaceMapper k8sNameSpaceMapper;


    private ExecutorService executorService;


    @Autowired
    HashMap<String, ConcurrentHashMap<String, ClusterFlushEntity>> clientKubePathMap;


    /**
     * flush db data to cache
     */
    private final ScheduledExecutorService flushToQueueExecutorService = Executors.newScheduledThreadPool(1);

    public void init(){
        // 批量创建k8s client
        createAllK8sClient();
        // 开始监控
        startWatch();
    }

    public void startWatch() {
        // 开启消费线程
        consumeAndUpdateTaskStatus();
        // 批量从db刷入到缓存中
        batchDataFlushFromDBToCache();
        // 批量从cache刷入队列
        batchFlushDataFromCacheToQueue();

    }

    /**
     * 缓存task 和 session 的所有信息
     */
    public void batchDataFlushFromDBToCache() {
        try {
            taskClusterMapService.batchCacheAllRunningTaskInst();
            taskClusterMapService.batchCacheAllRunningSessionInst();
            logger.info("CaffeineCache and clientKubeClusterIdIngressMap has cache all tasks.");
        } catch (Exception e) {
            logger.info("Batch flush data from db to cache failed ,{}",e.toString());
        }
    }

    public void createAllK8sClient() {
        try {
            List<K8sNameSpace> k8sNameSpaces = this.queryOnlineK8s();
            K8sClientManager k8sClientManager = new K8sClientManager();
            k8sNameSpaces.forEach(inst -> {
                k8sClientManager.createK8sClient(inst, watcherConfig.getCacheK8sClientNum());
            });
        } catch (Exception e) {
            logger.error("K8s client has create.");
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * batch flash from cache to queue
     */
    public void batchFlushDataFromCacheToQueue() {
        flushToQueueExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("Flush-Queue");
                Iterator<Map.Entry<String, ConcurrentHashMap<String, ClusterFlushEntity>>> iterator = clientKubePathMap.entrySet().iterator();
                while (iterator.hasNext()){
                    ConcurrentHashMap<String, ClusterFlushEntity> clusterIdEntityMap = iterator.next().getValue();
                    Iterator<Map.Entry<String, ClusterFlushEntity>> iterator1 = clusterIdEntityMap.entrySet().iterator();
                    while (iterator1.hasNext()){
                        ClusterFlushEntity value = iterator1.next().getValue();
                        try {
                            // 如果已经 OVER，设置为 YES
                            // 消费完事，设置为 YES，刷入缓存
                            // 遍历缓存，只要 YES 的，刷入队列，统一设置为 NO

                            // 过滤掉正在运行的任务
                            if (!value.getIfOver().get()){
                                return;
                            }
                            // 设置任务为排队状态
                            value.setIfOver(false);
                            // 如果插入队列超时，需要重新置为已经完成，便于后面重新运行
                            if (!blockingQueue.offer(value, 5000, TimeUnit.MILLISECONDS)) {
                                value.setIfOver(true);
                                throw new RuntimeException("Timeout while offering data to Queue, exceed " + 5000 + " ms, see ");
                            }
                            logger.debug("[{}] has been flush from cache to Queue",  value.getClusterId());
                        } catch (InterruptedException e) {
                            logger.error("Cluster: [{}] flush from cache to queue failed", value.getClusterId());
                            e.printStackTrace();
                        }
                    }

                }
                logger.info("All cached data has been flushed to queue.");
            }
        }, 0, watcherConfig.getFlushQueueIntervalSeconds(), TimeUnit.SECONDS);
    }

    public void consumeAndUpdateTaskStatus() {
        executorService = Executors.newFixedThreadPool(watcherConfig.getConsumeQueueThreadNum());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("Consume-Queue");
                while (true) {
                    ClusterFlushEntity clusterInst = null;
                    try {
                        clusterInst = blockingQueue.take();
                        // 如果还有为 YES 的，说明可能是异常刷入的已完成的对象
                        if(clusterInst.getIfOver().get()){
                             return;
                        }
                        Optional<String> optionalBody = taskRemoteService.doRequestToRestUrl(clusterInst);
                        if (optionalBody.isPresent()) {
                            parseAndUpdateFromRestUrlStatus(clusterInst, optionalBody.get());
                            // 只有 application 模式才能解决去远程remote集群运行状态，session模式只能判断
                        } else if (clusterInst.getRunModeType().equals(RunModeType.K8S_APPLICATION)){
                            parseAndUpdateFromK8sEventStatus(clusterInst);
                        } else {
                            // 查询不到的 session，之前全部置为None
                            batchChangeTaskStatus(clusterInst);
                        }
                        // 完事之后需要设置clusterInst为完成状态
//                        logger.info("Cluster: [{}] consume and parsed over.", clusterInst.getClusterId());
                        clusterInst.setIfOver(true);
                        // 如果在cache中被删除了，消费完之后将不会再遍历出来
                    } catch (Exception e) {
                        logger.error("Cluster: [{}] consume and parse failed", clusterInst);
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * session 模式找不到任务
     *
     * @param clusterInst
     */
    private void batchChangeTaskStatus(ClusterFlushEntity clusterInst) {
        clusterInst.getTaskFlushEntityMap().forEach((jobId, taskFlushEntity) -> {
            taskStatusService.updateTaskInstStatus(clusterInst, jobId, ExecStatus.NONE);
        });
    }

    public void parseAndUpdateFromRestUrlStatus(ClusterFlushEntity clusterInst, String body) {
        ObjectNode jsonNodes = JSONUtils.parseObject(body);
        ArrayNode arrayNode = (ArrayNode) jsonNodes.get("jobs");
        arrayNode.forEach(jobNode -> {
            String taskStatus = jobNode.get("state").asText();
            Optional<TaskRestUrlStatusConvertion.RestRunStatus> convertStatus = TaskRestUrlStatusConvertion.RestRunStatus.of(taskStatus);
            ExecStatus execStatus;
            if (!convertStatus.isPresent()) {
                logger.error("{} is not be identified by code,please check your code.", taskStatus);
                execStatus = ExecStatus.NONE;
            } else {
                execStatus = TaskRestUrlStatusConvertion.restStatusConvertToExec(convertStatus.get());
            }
            taskStatusService.updateTaskInstStatus(clusterInst, jobNode.get("jid").asText(), execStatus);
        });

    }

    /**
     * 如果是 session 模式的话，只能推测集群的状态，不能推测每个集群内部的任务的运行状态
     *
     * @param clusterInst
     */
    public void parseAndUpdateFromK8sEventStatus(ClusterFlushEntity clusterInst) {
        ExecStatus execStatus = taskRemoteService.doRemoteToK8s(clusterInst);
        clusterInst.getTaskFlushEntityMap().forEach((jobId, taskFlushEntity) -> {
            taskStatusService.updateTaskInstStatus(clusterInst, jobId, execStatus);
        });
    }

    public List<K8sNameSpace> queryOnlineK8s() {
        return k8sNameSpaceMapper.queryOnlineK8sNameSpace();
    }


    /**
     * accept http require to update cache and db
     * add to cache after submit
     */
    public synchronized boolean submitToUpdateCache(FlinkRunTaskInstance runTaskInstance) {
        try {
            // 然后创建 ingress
            ClusterFlushEntity clusterFlushEntity =
                taskClusterMapService.newTaskAddToCache(runTaskInstance);
            if(Asserts.isNull(clusterFlushEntity)){
                return false;
            }
            taskIngressService.addIngressRule(clusterFlushEntity);
            logger.info("Task ingress [{}] has been add into k8s client.",clusterFlushEntity.getRestUrl());
            return true;
        } catch (Exception e) {
            logger.error("Task => [{}] Mode=>[{}] add to watcher-cache and ingress failed",
                runTaskInstance.getFlinkTaskInstanceName(),
                runTaskInstance.getRunMode().getValue());
            e.printStackTrace();
        }
        return false;
    }
}
