package com.dpline.k8s.operator.watcher;

import com.dpline.alert.api.AlertEntity;
import com.dpline.alert.api.AlertManager;
import com.dpline.common.enums.*;
import com.dpline.common.params.K8sClusterParams;
import com.dpline.common.util.*;
import com.dpline.dao.entity.Cluster;
import com.dpline.dao.entity.Job;
import com.dpline.dao.mapper.ClusterMapper;
import com.dpline.dao.mapper.JobMapper;
import com.dpline.k8s.operator.service.TaskIngressOperateProxy;
import com.dpline.operator.common.WatcherConfig;
import com.dpline.operator.entity.SniffEvent;
import com.dpline.k8s.operator.job.ClusterFlushEntity;
import com.dpline.operator.entity.TaskFlushEntity;
import com.dpline.k8s.operator.k8s.K8sClusterManager;
import com.dpline.k8s.operator.service.TaskClusterMapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Component
public class TaskStatusManager {

    @Autowired
    TaskStatusRemoteProxy taskStatusRemoteProxy;

    @Autowired
    TaskClusterMapService taskClusterMapService;

    // TODO 动态识别 ingress 版本
    @Autowired
    TaskIngressOperateProxy taskIngressService;

    @Autowired
    K8sClusterManager k8sClusterManager;

    @Autowired
    ClusterMapper clusterMapper;
    
    @Autowired
    AlertManager alterManager;

    @Autowired
    JobMapper jobMapper;

    @Autowired
    WatcherConfig watcherConfig;

    private EventLoop eventLoop;

    private Logger logger = LoggerFactory.getLogger(TaskStatusManager.class);

    public void start(){
        eventLoop = new EventLoop();
        // 创建k8s 客户端
        createAllK8sClient();
        // 将数据刷入缓存和队列
        batchDataFlushFromDBToCache();
        // 开启消费进程
        eventLoop.start();
    }

    /**
     * 缓存task 和 session 的所有信息
     */
    public void batchDataFlushFromDBToCache() {
        try {
            List<SniffEvent> sniffEventList = taskClusterMapService.batchCacheAllRunningTaskInst();
            logger.info("ClientKubeClusterIdIngressMap has cache all tasks.Task num is {}",sniffEventList.size());
            sniffEventList.forEach(
                sniffEvent -> {
                    eventLoop.offerEvent(sniffEvent);
                    logger.info("Cluster sniffEvent [{}] has been add into read queue",sniffEvent.getClusterId());
                }
            );
        } catch (Exception e) {
            logger.info("Batch flush data from db to cache failed.\n",e);
        }
    }


    public void createAllK8sClient() {
        try {
            List<Cluster> clusterList = this.queryOnlineK8s();
            clusterList.stream().filter(cluster -> {
                ClusterType clusterType = ClusterType.of(cluster.getClusterType());
                return ClusterType.KUBERNETES.equals(clusterType);
            }).forEach(inst -> {
                K8sClusterParams k8sClusterParams = JSONUtils.parseObject(inst.getClusterParams(), K8sClusterParams.class);
                if(Asserts.isNull(k8sClusterParams)){
                    return;
                }
                k8sClusterManager.createK8sClient(inst.getId(),k8sClusterParams, watcherConfig.getCacheK8sClientNum());
            });
        } catch (Exception e) {
            logger.error("K8s client has create.");
            throw new RuntimeException(e.toString());
        }
    }

    public List<Cluster> queryOnlineK8s() {
        return clusterMapper.queryOnlineCluster("kubernetes");
    }

    /**
     * 嗅探任务状态
     */
    public boolean sniff(SniffEvent sniffEvent){
        Optional<ConcurrentHashMap<String, ClusterFlushEntity>> clusterIdEntityMap = taskClusterMapService.getClusterIdEntityMap(sniffEvent.getClusterId());
        // 如果集群中不存在 cluster，直接退出
        if(!clusterIdEntityMap.isPresent()){
            return false;
        }
        ClusterFlushEntity clusterFlushEntity = clusterIdEntityMap.get().get(sniffEvent.getClusterId());

        if(Asserts.isNull(clusterFlushEntity)){
            return false;
        }
        // 获取结果数据，然后根据结果数据和之前的数据进行比较，推断最终结果，然后根据最终结果，判断是否需要操作数据库，同步更新数据库状态，直接发送到队列中，
        Map<String, ExecStatus> remoteExecStatusMap = taskStatusRemoteProxy.remote(clusterFlushEntity);
        clusterFlushEntity.getTaskFlushEntityMap().forEach(
            (jobId,taskFlushEntity) -> {
                // 1. get new status
                ExecStatus newExecStatus = remoteExecStatusMap.get(jobId);
                // 2. update DB
                updateDB(taskFlushEntity,newExecStatus);
                // 3. alert
                callAlert(
                    taskFlushEntity,
                    newExecStatus);
                // 4. change task status
                changeTaskFlushEntity(taskFlushEntity,newExecStatus);
                // 5. clear resource if stopped
                if (newExecStatus.isStopped()) {
                    clearResource(clusterFlushEntity, taskFlushEntity.getRunJobId());
                }
            }
        );

        // application 模式下，有任务存在则 不能被清空; 无任务存在可以被清空
        // TODO session 模式下，需要结合操作符缓存判断是否继续监控
        // 从cluster 层面判断，发挥是否需要继续监控
        return !(clusterFlushEntity.getTaskFlushEntityMap().size() == 0
            && clusterFlushEntity.getRunModeType().getRunMode().equals(RunMode.APPLICATION));
    }

    private void changeTaskFlushEntity(TaskFlushEntity taskFlushEntity, ExecStatus newExecStatus) {
        //  if status change，update
        if(!taskFlushEntity.getExecStatus().equals(newExecStatus)){
            taskFlushEntity.setExecStatus(newExecStatus);
        }
        // if status is same, and is none, do nothing, else update timestamp
        if (!taskFlushEntity.getExecStatus().equals(newExecStatus) || !newExecStatus.isLost()) {
            taskFlushEntity.setCurrentTimeStamp(System.currentTimeMillis());
        }
    }

    public void callAlert(TaskFlushEntity taskFlushEntity,
                          ExecStatus newExecStatus) {
        AlertMode alertMode = taskFlushEntity.getAlertMode();
        Long alertInstanceId = taskFlushEntity.getAlertInstanceId();
        String taskName = taskFlushEntity.getTaskName();
        ExecStatus taskStatusBefore = taskFlushEntity.getExecStatus();
        // if equals, not alert
        if (newExecStatus.equals(taskFlushEntity.getExecStatus())) {
            return;
        }
        if (Asserts.isNull(taskFlushEntity.getAlertMode()) || alertMode.equals(AlertMode.NONE)){
            return;
        }
        // 不能没有告警实例
        if(Asserts.isNull(alertInstanceId) || alertInstanceId == 0){
            return;
        }
        // 任务停止告警模式，但是 任务未停止
        if (alertMode.equals(AlertMode.STOPPED) && !newExecStatus.isStopped()){
            return;
        }
        // 任务运行告警模式，但是 任务停止了
        if (alertMode.equals(AlertMode.RUNNING) && newExecStatus.isStopped()){
            return;
        }

        alterManager.init()
            .accept(
                new AlertEntity(
                    alertInstanceId,
                    taskFlushEntity.getJobId(),
                    taskName,
                    taskStatusBefore.name(),
                    newExecStatus.name()));
    }

    /**
     * 清理掉 缓存资源 和 ingress，
     *
     * @param clusterInst
     * @param runJobId
     */
    private void clearResource(ClusterFlushEntity clusterInst, String runJobId){
        try {
            // 清理任务缓存和操作记录
            taskClusterMapService.clearResource(clusterInst,runJobId);
            // 在application 模式下 删除 ingress 和 configmap
            if(clusterInst.getRunModeType().equals(RunModeType.K8S_APPLICATION)){
                taskIngressService.clearIngressRule(clusterInst);
                k8sClusterManager.deleteConfigMaps(
                    clusterInst.getClusterEntityId(),
                    clusterInst.getClusterId());
            }
        } catch (Exception ex) {
            logger.error("Task [{}] stopped, Clear resouce failed,",clusterInst.getClusterId(),ex);
        }
    }

    /**
     * session 手动清理资源
     *
     * @param clusterInst
     * @return
     */
    public boolean clearSessionResource(ClusterFlushEntity clusterInst){
        return taskClusterMapService.getClusterIdEntityMap(clusterInst.getClusterEntityId()).map(
            clusterIdEntityMap->{
                clusterIdEntityMap.remove(clusterInst.getClusterId());
                return taskIngressService.clearIngressRule(clusterInst);
            }
        ).orElse(false);
    }



    private void updateDB(TaskFlushEntity taskFlushEntity, ExecStatus newExecStatus) {
        try {
            // not change, return
            if (newExecStatus.equals(taskFlushEntity.getExecStatus())) {
                return;
            }
            // change and new status is none, return
            if(newExecStatus.equals(ExecStatus.NONE)){
                return;
            }
            // 状态变化，且新状态不是None，则更新数据库
            jobMapper.updateExecStatus(taskFlushEntity.getJobId(), newExecStatus.name());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交缓存，并创建探寻任务
     * @param job 
     * @return
     */
    public boolean submitToMonitor(Job job) {
        // 提交缓存
        logger.info("Add job [{}] into cache.",job.getJobName());
        ClusterFlushEntity clusterFlushEntity = taskClusterMapService.newTaskAddToCache(job);
        // 添加ingress
        if(Asserts.isNotNull(clusterFlushEntity)){
            logger.info("Ready add job [{}] into ingress.",job.getJobName());
            taskIngressService.addIngressRule(clusterFlushEntity);
            // 创建嗅探事件
            SniffEvent sniffEvent = new SniffEvent(clusterFlushEntity.getClusterEntityId(),
                clusterFlushEntity.getClusterId());
            if(eventLoop.offerEvent(sniffEvent)){
                logger.info("SniffEvent hash code: {}",System.identityHashCode(sniffEvent));
                logger.info("SniffEvent {} has add into read queue.", sniffEvent);
                return true;
            }
        }
        logger.error("Offer to read queue failed.");
        // 提交任务
        return false;
    }

    public class EventLoop {
        // 阻塞队列 用于准备数据开始
        private final LinkedBlockingDeque<SniffEvent> READ_QUEUE = new LinkedBlockingDeque<>(watcherConfig.getReadQueueCapacity());

        // 延迟队列延迟时间
        private final Long delayTime = watcherConfig.getWriteQueueDelayMilliSeconds();

        private final DelayQueue<SniffEvent> WRITE_QUEUE = new DelayQueue<>();

        public LinkedBlockingDeque<SniffEvent> getReadQueue() {
            return READ_QUEUE;
        }

        public DelayQueue<SniffEvent> getWriteQueue() {
            return WRITE_QUEUE;
        }

        private final Logger logger = LoggerFactory.getLogger(EventLoop.class);


        public void start(){
            this.push();
            logger.info("Push thread is running success");
            this.consume();
            logger.info("Consume thread is running success");
        }

        /**
         * 调度线程，用来消费从writeQueue 到 readQueue，形成闭环
         */
        public void push(){
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(()->{
                Thread.currentThread().setName("Push—Thread");
                while(true){
                    try {
                        SniffEvent sniffEvent = WRITE_QUEUE.take();
                        logger.debug("SniffEvent ready reset into READ_QUEUE, hash code: {}, [{}]", System.identityHashCode(sniffEvent),sniffEvent);
                        READ_QUEUE.offer(sniffEvent, 5000, TimeUnit.MILLISECONDS);
                    } catch (Exception ex) {
                        logger.error(ExceptionUtil.exceptionToString(ex));
                    }
                }
            });
        }

        /**
         * 从读队列消费消息，然后插入到写队列
         */
        public void consume() {
            int consumeQueueThreadNum = watcherConfig.getConsumeQueueThreadNum();

            ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                consumeQueueThreadNum,
                consumeQueueThreadNum,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1024),
                ThreadUtils.threadFactory("Consume-Thread"),
                new ThreadPoolExecutor.AbortPolicy());
            IntStream.range(0, consumeQueueThreadNum).forEach(
                value -> {
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                SniffEvent sniffEvent = null;
                                try {
                                    sniffEvent = READ_QUEUE.take();
                                    logger.debug("SniffEvent ready to sniff, hash code: {}, [{}]", System.identityHashCode(sniffEvent),sniffEvent);
                                    boolean isAlive = sniff(sniffEvent);
                                    if(isAlive){
                                        // 重设过期时间,用于延迟队列
                                        sniffEvent.resetExpireTime(delayTime,TimeUnit.MILLISECONDS);
                                        WRITE_QUEUE.offer(sniffEvent);
                                    }
                                } catch (Exception e) {
                                    logger.error("Cluster: [{}] consume and parse failed", sniffEvent);
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            );
        }
        /**
         * 将 clusterFlashEntity 提交到队列中
         */
        public boolean offerEvent(SniffEvent sniffEvent) {
            return READ_QUEUE.offer(sniffEvent);
        }
    }

}
