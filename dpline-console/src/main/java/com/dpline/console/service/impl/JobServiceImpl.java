package com.dpline.console.service.impl;

import com.dpline.common.Constants;
import com.dpline.common.MotorKeyConstants;
import com.dpline.common.enums.*;
import com.dpline.common.store.Minio;
import com.dpline.common.params.*;
import com.dpline.common.request.*;
import com.dpline.common.util.*;
import com.dpline.console.annotation.OperateTypeAnnotation;
import com.dpline.console.handler.DeployExecutor;
import com.dpline.console.handler.TaskOperatorFactory;
import com.dpline.console.service.GenericService;
import com.dpline.console.service.NettyClientService;
import com.dpline.console.util.ContextUtils;
import com.dpline.dao.domain.SourceConfig;
import com.dpline.dao.dto.DsConfig;
import com.dpline.dao.dto.FileDto;
import com.dpline.dao.dto.JobDto;
import com.dpline.dao.dto.JobRunConfig;
import com.dpline.dao.entity.*;
import com.dpline.dao.entity.FlinkVersion;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.mapper.JobMapper;
import com.dpline.dao.rto.JobRto;
import com.dpline.remote.command.*;
import com.dpline.remote.future.InvokeCallback;
import com.dpline.remote.future.ResponseFuture;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.dpline.common.util.TaskPathResolver.SAVEPOINT_DIR_FORMAT;
import static com.dpline.console.socket.FileDataListener.DEPLOY_FAILED_FLAG;
import static com.dpline.console.socket.FileDataListener.DEPLOY_SUCCESS_FLAG;


@Service
public class JobServiceImpl extends GenericService<Job, Long> {

    private static final String JOB_NAME_FORMAT = "%s_%s";

    @Autowired
    Minio minio;

    @Autowired
    DockerImageServiceImpl dockerImageServiceImpl;

    @Autowired
    FolderServiceImpl folderServiceImpl;

    @Autowired
    ProjectServiceImpl projectServiceImpl;

    @Autowired
    JarFileServiceImpl jarFileServiceImpl;

    @Autowired
    MainResourceFileServiceImpl mainResourceFileServiceImpl;

    @Autowired
    FileServiceImpl fileServiceImpl;

    @Autowired
    ClusterServiceImpl clusterServiceImpl;

    @Autowired
    FlinkVersionServiceImpl flinkVersionServiceImpl;

    @Autowired
    TaskOperatorFactory taskOperatorFactory;

    @Autowired
    NettyClientService nettyClientService;

    @Autowired
    SavePointServiceImpl savePointServiceImpl;

    @Autowired
    DplineJobOperateLogImpl dplineJobOperateLogImpl;

    @Autowired
    ProjectUserServiceImpl projectUserServiceImpl;

    /**
     * 操作符 缓存，
     * 如果操作了 任务启动，如果queryJob，任务状态还为停止，那么就返回 SUBMITTING
     * 如果操作了 任务停止，如果queryJob，任务状态还在运行，那么就返回 STOPPING
     * 持续时长为 10s，10s后此缓存失效。
     */
    private final ConcurrentHashMap<Long, OperationsEnumEntity> operateMap = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    /**
     * 展示任务的配置
     * if (key === 'clusterType') {
     *    text = '任务运行集群类型'
     *    flag = true
     *  } else if (key === 'flinkVersion') {
     *    text = 'flink版本'
     *    flag = true
     *  } else if (key === 'runMode') {
     *             text = '任务运行模式'
     * @param jobId
     * @param confType
     * @return
     */
    public Result<Object> getJobConf(Long jobId, String confType) {
        Result<Object> result = new Result<>();
        Optional<ConfigType> configTypeOptional = ConfigType.of(confType);
        return configTypeOptional.map(configType -> {
            Job job = this.getMapper().selectById(jobId);
            switch (configType){
                case JOB_CONFIG:
                    JobRunConfig jobRunConfig = new JobRunConfig();
                    Long clusterId = job.getClusterId();
                    Cluster cluster = clusterServiceImpl.getMapper().selectById(clusterId);
                    jobRunConfig.setClusterName(cluster.getClusterName());
                    Long motorVersionId = job.getMotorVersionId();
                    FlinkVersion flinkVersion = flinkVersionServiceImpl.getMapper().selectById(motorVersionId);
                    DockerImage dockerImage = dockerImageServiceImpl.getMapper().selectById(job.getImageId());
                    jobRunConfig.setMotorVersion(flinkVersion.getRealVersion());
                    jobRunConfig.setRunModeType(job.getRunModeType());
                    jobRunConfig.setImageName(dockerImage.getImageName());
                    RuntimeOptions runtimeOptions = JSONUtils.parseObject(job.getRuntimeOptions(), RuntimeOptions.class);
                    jobRunConfig.setJobManagerCpus(runtimeOptions.getJobManagerCpu());
                    jobRunConfig.setTaskManagerCpus(runtimeOptions.getTaskManagerCpu());
                    jobRunConfig.setDefaultParallelism(runtimeOptions.getParallelism());
                    jobRunConfig.setJobManagerMemory(runtimeOptions.getJobManagerMem());
                    jobRunConfig.setTaskManagerMemory(runtimeOptions.getTaskManagerMem());
                    return result.ok().setData(jobRunConfig);
                case FLINK_CONFIG:
                    Map<String, Object> flinkYamlMap = JobConfigSerialization.parseYamlToMap(job.getOtherRuntimeConfig());
                    return result.ok().setData(flinkYamlMap);
                case DS_CONFIG:
                    // DS 配置
                    String fileType = job.getFileType();
                    if(FileType.DATA_STREAM.equals(FileType.of(fileType))){
                        Long mainJarId = job.getMainJarId();
                        JarFile jarFile = jarFileServiceImpl.getMapper().selectById(mainJarId);
                        DsConfig dsConfig = new DsConfig();
                        dsConfig.setAppArgs(job.getAppArgs());
                        dsConfig.setMainJarName(jarFile.getJarName());
                        dsConfig.setMainClass(job.getMainClass());
                        return result.ok().setData(dsConfig);
                    }
                    return result;
                case SOURCE_CONFIG:
                    String sourceContent = job.getSourceContent();
                    SourceConfig sourceConfig = JSONUtils.parseObject(sourceContent, SourceConfig.class);
                    if(Asserts.isNull(sourceConfig)){
                        return result;
                    }
                    List<SourceConfig.MainResourceDepend> jarDependList = sourceConfig.getJarDependList();
                    HashMap<String, String> resourceTypeMap = new HashMap<>();
                    jarDependList.forEach(jarDepend->{
                        MainResourceFile mainResourceFile = mainResourceFileServiceImpl.getMapper().selectById(jarDepend.getMainResourceId());
                        String name = mainResourceFile.getName();
                        String jarFunctionType = mainResourceFile.getJarFunctionType();
                        resourceTypeMap.put(name,jarFunctionType);
                    });
                    return result.ok().setData(resourceTypeMap);
                default:
                    throw new IllegalStateException("Unexpected value: " + configType);
            }
        }).orElse(result);
    }

    @Data
    static class OperationsEnumEntity {
        private Long jobId;
        private OperationsEnum operationsEnum;
        private Long timeStamp;

        public OperationsEnumEntity(Long jobId, OperationsEnum operationsEnum, Long timeStamp) {
            this.jobId = jobId;
            this.operationsEnum = operationsEnum;
            this.timeStamp = timeStamp;
        }
    }


    public JobServiceImpl(@Autowired JobMapper jobMapper) {
        super(jobMapper);
        // 定时删除操作记录
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Collection<OperationsEnumEntity> values = operateMap.values();
                values.forEach(operationsEnumEntity -> {
                    // 6s超时删除
                    if (System.currentTimeMillis() - operationsEnumEntity.getTimeStamp() > 6000L) {
                        operateMap.remove(operationsEnumEntity.getJobId());
                    }
                });
            }
        }, 5, 2, TimeUnit.SECONDS);
    }

    public JobMapper getMapper() {
        return (JobMapper) genericMapper;
    }

    public List<Job> selectByDockerImageId(long dockerImageId) {
        return this.getMapper().selectByDockerImageId(dockerImageId);
    }

    public List<Job> selectByClusterId(Long clusterId) {
        return this.getMapper().selectByClusterId(clusterId);
    }

    public synchronized Result<Object> listJob(JobRto jobRto) {
        Result<Object> result = new Result<>();
        if(!projectUserServiceImpl.hasQueryPerm(ContextUtils.get().getUser(), jobRto.getProjectId())){
            putMsg(result,Status.USER_NO_OPERATION_PERM);
            return result;
        }
        jobRto.getVo().setJobName(StringUtils.changeWildcard(jobRto.getVo().getJobName()));
        Long folderId = Optional.of(jobRto).map(m -> m.getFolderId()).orElse(null);
        if (Objects.nonNull(folderId)) {
            Set<Long> folderIds = new HashSet<>();
            folderServiceImpl.queryFolderIdAndChildFolderId(folderId, folderIds);
            if (CollectionUtils.isNotEmpty(folderIds)) {
                jobRto.setFolderIds(new ArrayList<>(folderIds));
            }
        }
        Pagination<Job> pagination = Pagination.getInstanceFromRto(jobRto);
        this.executePagination(this::queryJobs, pagination);
        List<Job> rows = pagination.getRows();
        rows.forEach(job -> {
            initJobRestUrl(job);
            job.setRunModeType(RunModeType.of(job.getRunModeType()).getRunMode().getValue());
            // 解压job flink 额外配置
            job.setOtherRuntimeConfig(
                JobConfigSerialization.unzipJobContent(job.getOtherRuntimeConfig())
            );
            //获取Grafana的跳转地址
            combineGrafanaUrl(job);
            // 设置内存
            setMemoryConfig(job);
            // 纠正 展示的执行状态
            correctExecStatusDisplay(job);
        });
        pagination.setRows(rows);
        return result.ok().setData(pagination);
    }

    /**
     * TODO 解析 job rest-url,如果是 session 模式，则自动生成 rest-url
     * @param job
     */
    void initJobRestUrl(Job job){
        String clusterParams = job.getClusterParams();
        K8sClusterParams k8sClusterParams = JSONUtils.parseObject(clusterParams, K8sClusterParams.class);
        if(Asserts.isNull(k8sClusterParams)){
            logger.warn("job [{}] cluster Params is not exists",job.getJobName());
            return;
        }
        //Session 模式下，jobName 需要设置为session 的 name
        job.setRestUrl(TaskPathResolver.getWebViewUrl(
                k8sClusterParams.getNameSpace(),
                k8sClusterParams.getIngressHost(),
                job.getJobName(),
                job.getRunJobId()
                )
        );

    }

    private void correctExecStatusDisplay(Job job) {
        ExecStatus execStatus = ExecStatus.of(job.getExecStatus());
        // 任务五秒内，都需要判断预显示的值
        OperationsEnumEntity operationsEnumEntity = operateMap.get(job.getId());
        if (Asserts.isNull(operationsEnumEntity)) {
            return;
        }
        // 如果操作了启动，但是任务未处于运行状态，那么显示正在提交。
        if (operationsEnumEntity.getOperationsEnum().equals(OperationsEnum.START) && !execStatus.isRunning()) {
            job.setExecStatus(ExecStatus.SUBMITTING.name());
        }
        // 如果操作了停止，但是任务处于运行状态，那么显示正在停止
        if (operationsEnumEntity.getOperationsEnum().equals(OperationsEnum.STOP) && execStatus.isRunning()) {
            job.setExecStatus(ExecStatus.STOPPING.name());
        }
    }

    private void setMemoryConfig(Job job) {
        RuntimeOptions runtimeOptions = JSONUtils.parseObject(job.getRuntimeOptions(), RuntimeOptions.class);
        job.setTaskManagerMem(runtimeOptions.getTaskManagerMem());
        job.setJobManagerMem(runtimeOptions.getJobManagerMem());
        job.setParallelism(parseParallelism(job.getRuntimeOptions()));
    }

    private List<Job> queryJobs(Pagination<Job> p) {
        return this.getMapper().queryJobs(p);
    }

    private void combineGrafanaUrl(Job job) {
        String jobName = job.getJobName();
        Long projectId = job.getProjectId();
        jobName = String.format(JOB_NAME_FORMAT, projectId, jobName);

        String prometheusUrl = CommonProperties.getMonitorPrometheusUrl(jobName);
        String formatPrometheusUrl = String.format(prometheusUrl, jobName);
        job.setGrafanaUrl(formatPrometheusUrl);

        //返回全路径（前端有需要截取最后一个文件夹名称作为目录列）
        if (Objects.nonNull(job.getFileId())) {
            Long folderId = job.getFolderId();
            String strB = fileServiceImpl.assembleFilePath(folderId, new StringBuilder());
            String fullPath = String.format("/作业开发/%s", strB).replaceAll("/$", "");
            if (StringUtils.isNotBlank(fullPath)) {
                job.setFullPath(fullPath);
            }
        }
    }

    /**
     * 获取 slot 数量
     *
     * @return
     */
    public Integer parseSlotNum(String otherRuntimeConfig) {
        if (StringUtils.isBlank(otherRuntimeConfig)) {
            return 1;
        }
        String flinkYaml = DeflaterUtils.unzipString(otherRuntimeConfig);
        Map<String, Object> motorConfigMap = YamlReader.getInstance().readText(flinkYaml);
        String taskSlotsNum = motorConfigMap.get(MotorKeyConstants.TASKMANAGER_NUMBEROFTASKSLOTS).toString();

        if (StringUtils.isBlank(taskSlotsNum)) {
            return 1;
        }
        return Integer.parseInt(taskSlotsNum);
    }

    /**
     * @return 获取并行度
     */
    public Integer parseParallelism(String config) {
        RuntimeOptions runtimeOptions = JSONUtils.parseObject(config, RuntimeOptions.class);
        if (Asserts.isNull(runtimeOptions)) {
            return 1;
        }
        return runtimeOptions.getParallelism();
    }

    /**
     * 部署任务
     *
     * @param jobRto
     * @return
     */
    @OperateTypeAnnotation(value=OperationsEnum.DEPLOY, jobId="#{jobRto}")
    public synchronized Result<Object> deployJob(JobRto jobRto) {
        // 将路径改为原来的路径
        Result<Object> result = new Result<>();
        logger.info("任务准备部署,任务状态改变");
        Job job = this.getMapper().selectById(jobRto.getId());
        job.setDeployed(2);
        updateSelective(job);
        // 正在部署
        try {
            // 任务状态设置为3，只要有任何异常，都会判为部署失败
            job.setDeployed(3);
            Long motorVersionId = job.getMotorVersionId();
            Cluster cluster = clusterServiceImpl.getMapper().selectById(job.getClusterId());
            FlinkVersion flinkVersion = flinkVersionServiceImpl.getMapper().selectById(motorVersionId);

            if (Asserts.isNull(cluster)) {
                putMsg(result, Status.JOB_CLUSTER_NOT_EXIST);
                return result;
            }

            if (Asserts.isNull(flinkVersion)) {
                putMsg(result, Status.JOB_FLINK_NOT_EXIST);
                return result;
            }
            // find effect main jar
            JarFile mainEffectJar = jarFileServiceImpl.getMapper().findMainEffectJar(job.getMainResourceId());
            if (Asserts.isNull(mainEffectJar)) {
                logger.error("Main resource [{}] is not find.", job.getMainResourceId());
                putMsg(result, Status.MAIN_FILE_NOT_EXIST_ERROR);
                return result;
            }
            job.setMainJarId(mainEffectJar.getId());
            logger.info("Main jar Id [{}] has been set into job.", mainEffectJar.getId());
            DeployExecutor deployHandler = getDeployExecutor(job);
            if (Asserts.isNull(deployHandler)) {
                putMsg(result, Status.JOB_MODE_NOT_SUPPORT);
                return result;
            }
            // 将任务配置转化为 jobConfig
            // 任务部署
            JobDto jobDto = new JobDto();
            // 查询所有cluster 的信息
            BeanUtils.copyProperties(job, jobDto);
            jobDto.setCluster(cluster);
            jobDto.setFlinkVersion(flinkVersion);
            String deployPath = deployHandler.deploy(jobDto);
            if (StringUtils.isNotBlank(deployPath)) {
                job.setDeployed(1);
                logger.info(DEPLOY_SUCCESS_FLAG);
                result.ok().setData(job);
            } else {
                logger.info(DEPLOY_FAILED_FLAG);
                putMsg(result, Status.JOB_DEPLOY_ERROR);
            }
        } catch (Exception exception) {
            logger.error("Task deploy failed.\n ", exception);
            logger.info(DEPLOY_FAILED_FLAG);
            putMsg(result, Status.JOB_DEPLOY_ERROR);
        } finally {
            updateSelective(job);
        }
        return result;
    }

    public List<Job> getJobByFileId(FileDto fileDto) {
        return this.getMapper().selectByFileId(fileDto.getId());
    }

    public Map<Long, byte[]> isOnlineFile(Collection<Long> fileIdList) {
        HashMap<Long, byte[]> objectObjectHashMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(fileIdList)) {
            List<Job> jobList = this.getMapper().selectByBatchFileIds(fileIdList);
            jobList.forEach(job -> {
                if (!objectObjectHashMap.containsKey(job.getFileId())) {
                    objectObjectHashMap.put(job.getFileId(), new byte[0]);
                }
            });
        }
        return objectObjectHashMap;
    }

    /**
     * @param jobRto 删除 任务
     * @return
     */
    public Result<Object> deleteJob(JobRto jobRto) {
        // 如果任务正在运行，不允许下线
        // 首先删除线上应用Jar包，然后删除 job 表记录，不删除日志
        Result<Object> result = new Result<>();
        Job job = this.getMapper().selectById(jobRto.getId());
        String execStatus = job.getExecStatus();
        ExecStatus of = ExecStatus.of(execStatus);
        if (of.isStopping() || of.isRunning()) {
            putMsg(result, Status.JOB_RUNNING_ERROR);
            return result;
        }
        DeployExecutor deployHandler = getDeployExecutor(job);
        if (Asserts.isNull(deployHandler)) {
            putMsg(result, Status.JOB_MODE_NOT_SUPPORT);
            return result;
        }
        deployHandler.clear(job);
        this.getMapper().deleteById(jobRto.getId());
        // 删除 线上 savepoint 地址
        List<JobSavepoint> jobSavepoints = this.savePointServiceImpl.getMapper().selectByJobId(job.getId());
        try {
            jobSavepoints.forEach(jobSavepoint -> {
                try {
                    minio.removeObjects(jobSavepoint.getSavepointPath().replace(minio.getFileSystemPrefix(),Constants.BLACK));
                } catch (Exception exception){
                    throw new RuntimeException(exception);
                }
            });
        } catch (Exception exception){
            logger.error("Delete savepoint failed.", exception);
        }

        this.savePointServiceImpl.getMapper().deleteSavePointByJobId(job.getId());
        return result.ok();
    }

    public DeployExecutor getDeployExecutor(Job job) {
        RunModeType runModeType = RunModeType.of(job.getRunModeType());
        FileType fileType = FileType.of(job.getFileType());
        return taskOperatorFactory.getDeployExecutor(fileType, runModeType);

    }


    @Transactional(rollbackFor = Exception.class)
    public Result<Object> updateAlertConfig(Long jobId, String alertMode, long alertInstanceId) {
        Result<Object> result = new Result<>();
        Job job = this.getMapper().selectById(jobId);
        putMsg(result, Status.ALERT_CONFIG_EDIT_ERROR);
        if (Asserts.isNull(AlertMode.of(alertMode))) {
            return result;
        }

        if (remoteChangeAlertConfig(job, alertMode, alertInstanceId)) {
            this.getMapper().updateAlertConfig(job.getId(), alertMode, alertInstanceId);
            return result.ok();
        }
        return result;
    }

    /**
     * 更新 告警配置
     *
     * @param job             将要更新的任务
     * @param alertMode       告警模式
     * @param alertInstanceId 告警对象Id
     * @return
     */
    public boolean remoteChangeAlertConfig(Job job, String alertMode, long alertInstanceId) {
        // not change,return true
        if (alertMode.equals(job.getAlertMode()) && alertInstanceId == job.getAlertInstanceId()) {
            return true;
        }
        // not run, return true
        if (ExecStatus.of(job.getExecStatus()).isStopped()
                || Asserts.isNull(job.getExecStatus())
                || ExecStatus.of(job.getExecStatus()).isLost()) {
            return true;
        }
        // change and run, send alert change
        TaskAlertEditResponseCommand taskAlertEditResponseCommand = (TaskAlertEditResponseCommand) nettyClientService.sendCommand(ClusterType.KUBERNETES,new TaskAlertEditCommand(
            TaskAlertEditRequest.builder()
                .jobId(job.getId())
                .runJobId(job.getRunJobId())
                .clusterId(job.getJobName())
                //TODO SESSION 模式下传Session名称参数
                .alertMode(AlertMode.of(alertMode))
                .alertInstanceId(alertInstanceId)
                .build()
        ), TaskAlertEditResponseCommand.class);
        if (Asserts.isNotNull(taskAlertEditResponseCommand)
            && ResponseStatus.SUCCESS.equals(taskAlertEditResponseCommand.
            getTaskAlertEditResponse()
            .getResponseStatus())) {
            return true;
        }
        // not success
        return false;
    }

    /**
     * 任务开启
     *
     * @param jobRto
     * @return
     */
    @OperateTypeAnnotation(value=OperationsEnum.START, jobId="#{jobRto}")
    public synchronized Result<Object> startJob(JobRto jobRto) {
        Result<Object> result = new Result<>();
        Job job = this.getMapper().selectById(jobRto.getId());
        if(Asserts.isNull(job)){
            putMsg(result, Status.JOB_NOT_EXIST);
            return result;
        }
        if (Asserts.isNotNull(operateMap.get(jobRto.getId()))) {
            logger.warn("Operate cache has start operator, please try again after 10s.");
            putMsg(result, Status.FLINK_TASK_SUBMITTING_WAIT_ERROR);
            return result;
        }
        Optional<FlinkRequest> flinkSubmitRequest = createSubmitRequest(job, jobRto.getSavepointPath(),result);
        // 如果任务 执行失败，需要设置为 FAILED
        if(!flinkSubmitRequest.isPresent()){
            logger.warn("create submit request error,please check your arguments.");
            return result;
        }
        FlinkRequest req = flinkSubmitRequest.get();
        logger.info("SubmitRequest ==> {}",req.toString());
        try {
            // 操作添加缓存
            operateMap.put(
                jobRto.getId(),
                new OperationsEnumEntity(
                    jobRto.getId(),
                    OperationsEnum.START,
                    System.currentTimeMillis())
            );
            logger.info("Operate has been insert into cache");

            nettyClientService.sendCommandAsync(
                    ClusterType.KUBERNETES,
                new TaskRunCommand(JSONUtils.toJsonString(req),
                                   req.getRunModeType().getClusterType().getValue(),
                                   MDC.get(Constants.TRACE_ID)
                    ),
                new InvokeCallback() {
                    @Override
                    public void operationComplete(ResponseFuture responseFuture) {
                        Command responseCommand = responseFuture.getResponseCommand();
                        TaskRunResponseCommand taskRunResponseCommand = JSONUtils.parseObject(responseCommand.getBody(), TaskRunResponseCommand.class);
                        // 任务执行失败
                        if (Asserts.isNotNull(taskRunResponseCommand) &&
                            taskRunResponseCommand.getSubmitResponse().getResponseStatus().equals(ResponseStatus.FAIL)) {
                            // 失败即移除
                            operateMap.remove(jobRto.getId());
                            job.setExecStatus(ExecStatus.FAILED.name());
                            update(job);
                        }

                    }
                }
            );
        } catch (Exception e) {
            operateMap.remove(jobRto.getId());
            logger.error("Remote operator-server failed. {}",ExceptionUtil.exceptionToString(e));
        }
        return result.ok();
    }


    /**
     * create submit Request
     *
     * @param job
     * @param savepointPath
     * @return
     */
    private Optional<FlinkRequest> createSubmitRequest(Job job, String savepointPath,Result<Object> result) {
        // Flink 任务提交请求配置
        RunModeType runModeType = RunModeType.of(job.getRunModeType());
        // flink home
        FlinkVersion flinkVersion = flinkVersionServiceImpl.getMapper().selectById(job.getMotorVersionId());
        if(Asserts.isNull(flinkVersion)){
            putMsg(result,Status.FLINK_VERSION_NOT_EXISTS);
            return Optional.empty();
        }

        FlinkHomeOptions flinkHomeOptions = FlinkHomeOptions
            .builder()
            .flinkPath(CommonProperties.pathDelimiterResolve(flinkVersion.getFlinkPath()))
            .realVersion(flinkVersion.getRealVersion())
            .build();
        // cluster
        Cluster cluster = clusterServiceImpl.getMapper().selectById(job.getClusterId());
        if (Asserts.isNull(cluster)) {
            logger.warn("Cluster [{}] 不存在",job.getClusterId());
            putMsg(result,Status.MAIN_JAR_RESOURCE_NOT_EXISTS,job.getMainJarId());
            return Optional.empty();
        }
        JarFile jarFile = jarFileServiceImpl.getMapper().selectById(job.getMainJarId());
        if (Asserts.isNull(jarFile)) {
            logger.warn("Jar文件 => ID[{}],已经不存在，需要重新部署",job.getMainJarId());
            putMsg(result,Status.MAIN_JAR_RESOURCE_NOT_EXISTS,job.getMainJarId());
            return Optional.empty();
        }
        // run time config
        RuntimeOptions runtimeOptions = JSONUtils.parseObject(job.getRuntimeOptions(), RuntimeOptions.class);
        if (Asserts.isNull(runtimeOptions)) {
            logger.error("运行配置解析失败,RuntimeOptions=>{}", runtimeOptions.toString());
            putMsg(result,Status.RUNTIME_OPTIONS_PARSE_ERROR);
            return Optional.empty();
        }
        Map<String, Object> otherRunTimeConfigMap = null;
        try {
            otherRunTimeConfigMap = JobConfigSerialization.parseYamlToMap(job.getOtherRuntimeConfig());
        } catch (Exception exception){
            logger.error(ExceptionUtil.exceptionToString(exception));
            putMsg(result,Status.RUNTIME_OPTIONS_PARSE_ERROR);
            return Optional.empty();
        }

        if (StringUtils.isNotBlank(savepointPath)) {
            otherRunTimeConfigMap.put(MotorKeyConstants.EXECUTION_SAVEPOINT_PATH, savepointPath);
        }
        // 遍历 otherRunTimeConfigMap，将下面的 ${jobName} 变为 jobName
        runtimeOptions.setOtherParams(otherRunTimeConfigMap);
        // job definition
        JobDefinitionOptions jobDefinitionOptions = JobDefinitionOptions.builder()
            .jobId(job.getId())
            .runJobId(job.getRunJobId())
            .projectId(job.getProjectId())
            .mainClass(job.getMainClass())
            .appArgs(job.getAppArgs())
            .fileType(FileType.of(job.getFileType()))
            .jobName(job.getJobName())
            // 任务远程部署地址
            .jarPath(TaskPathResolver.getFlinkRunMainJarPath(jarFile.getJarName()))
            .deployAddress(TaskPathResolver.getTaskRemoteDeployDir(job.getProjectId(), job.getId()))
            //s3://flink/checkpoint/projectId/jobId/runJobId/chk-xxxx/meta-
            .defaultCheckPointDir(String.format(SAVEPOINT_DIR_FORMAT,
                minio.getDefaultFs(),
                minio.getBucketName(),
                TaskPathResolver.getJobDefaultCheckPointDir(job.getProjectId(), job.getId())))
            .build();

        switch (runModeType) {
            case LOCAL:
            case REMOTE:
            case YARN_APPLICATION:
            case YARN_SESSION:
            case K8S_SESSION:
                return Optional.empty();
            case K8S_APPLICATION:
                // 查询docker image 信息
                DockerImage dockerImage = dockerImageServiceImpl.getMapper().selectById(job.getImageId());
                if (Asserts.isNull(dockerImage)) {
                    putMsg(result,Status.JOB_IMAGE_NOT_EXISTS);
                    return Optional.empty();
                }
                // 查询 k8s 的信息
                K8sClusterParams k8sClusterParams = JSONUtils.parseObject(cluster.getClusterParams(), K8sClusterParams.class);
                logger.info("K8sClusterParams: {}",k8sClusterParams);
                if (Asserts.isNull(k8sClusterParams)) {
                    logger.error("k8s cluster params is null");
                    putMsg(result,Status.JOB_CLUSTER_NOT_EXIST);
                    return Optional.empty();
                }
                K8sOptions k8sOptions = K8sOptions.builder()
                        .imageAddress(dockerImage.getImageName())
                        .clusterId(job.getJobName())
                        .kubePath(k8sClusterParams.getKubePath())
                        .exposedType(ExposedType.CLUSTER_IP)
                        .selectNode(k8sClusterParams.getExtraParam()
                                    .stream()
                                    .collect(
                                    Collectors.toMap(entity -> entity.get("key"), entity -> entity.get("value"))
                                ))
                        .serviceAccount(k8sClusterParams.getServiceAccount())
                        .nameSpace(k8sClusterParams.getNameSpace())
                        .ingressHost(k8sClusterParams.getIngressHost())
                        .ingressName(k8sClusterParams.getIngressName())
                        .build();
                return Optional.ofNullable(FlinkK8sRemoteSubmitRequest.builder()
                    .k8sOptions(k8sOptions)
                    .flinkHomeOptions(flinkHomeOptions)
                    .jobDefinitionOptions(jobDefinitionOptions)
                    .resourceOptions(runtimeOptions)
                    .runModeType(runModeType)
                    .build());
        }
        return Optional.empty();
    }


    /**
     * 任务停止接口
     *
     * @param jobRto
     * @return
     */
    @OperateTypeAnnotation(value=OperationsEnum.STOP,jobId="#{jobRto}")
    public synchronized Result<Object> stopJob(JobRto jobRto) {
        Result<Object> result = new Result<>();
        if (Asserts.isNotNull(operateMap.get(jobRto.getId()))) {
            putMsg(result, Status.FLINK_TASK_STOPPING_ERROR);
            return result;
        }
        Job job = this.getMapper().selectById(jobRto.getId());
        Optional<FlinkStopRequest> flinkStopRequestOpt = createStopRequest(job, jobRto.getSavepointPath());
        if (!flinkStopRequestOpt.isPresent()) {
            putMsg(result, Status.CREATE_START_REQUEST_ERROR);
            return result;
        }
        FlinkStopRequest flinkStopRequest = flinkStopRequestOpt.get();
        logger.info("SubmitRequest ==> {}", flinkStopRequest.toString());
        // 操作添加缓存
        operateMap.put(jobRto.getId(),
            new OperationsEnumEntity(jobRto.getId(),
                OperationsEnum.STOP,
                System.currentTimeMillis()));

        TaskStopResponseCommand taskStopResponseCommand =
            (TaskStopResponseCommand) nettyClientService.sendCommand(
                    ClusterType.KUBERNETES,
                new TaskStopCommand(flinkStopRequest,
                    flinkStopRequest.getRunModeType().getClusterType().getValue(),
                    MDC.get(Constants.TRACE_ID)),
                TaskStopResponseCommand.class);
        if (Asserts.isNull(taskStopResponseCommand)) {
            job.setExecStatus(ExecStatus.STOPPING.name());
            putMsg(result, Status.STOP_FLINK_TASK_INSTANCE_ERROR);
            return result;
        }
        StopResponse stopResponse = taskStopResponseCommand.getStopResponse();
        if (Asserts.isNull(stopResponse) || ResponseStatus.FAIL.equals(stopResponse.getResponseStatus())) {
            // 失败即移除缓存
            operateMap.remove(jobRto.getId());
            putMsg(result, Status.STOP_FLINK_TASK_INSTANCE_ERROR);
            return result;
        }
        result.setData(job);
        return result.ok();
    }

    /**
     * 任务停止
     *
     * @param job
     * @return
     */
    private Optional<FlinkStopRequest> createStopRequest(Job job, String savePointPath) {
        RunModeType runModeType = RunModeType.of(job.getRunModeType());
        FlinkVersion flinkVersion = flinkVersionServiceImpl.getMapper().selectById(job.getMotorVersionId());
        Cluster cluster = clusterServiceImpl.getMapper().selectById(job.getClusterId());
        String clusterParams = cluster.getClusterParams();
        switch (runModeType) {
            case LOCAL:
            case REMOTE:
            case YARN_APPLICATION:
            case YARN_SESSION:
            case K8S_SESSION:
                // sessionId 作为 imageId
                //TODO session 模式是sessionName，application模式是 jobName
                //     flinkStopRequest.setClusterId(session.getSessionName());
                return Optional.empty();
            case K8S_APPLICATION:
                K8sClusterParams kubePathCluster = JSONUtils.parseObject(clusterParams, K8sClusterParams.class);
                if (Asserts.isNull(kubePathCluster)) {
                    return Optional.empty();
                }
                FlinkStopRequest flinkStopRequest = FlinkStopRequest.builder()
                        .withSavePointAddress(StringUtils.isNotBlank(savePointPath))
                        .jobId(job.getId())
                        .projectId(job.getProjectId())
                        .runJobId(job.getRunJobId())
                        .runModeType(runModeType)
                        .clusterId(job.getJobName())
                        .nameSpace(kubePathCluster.getNameSpace())
                        .kubePath(kubePathCluster.getKubePath())
                        .savePointAddress(savePointPath)
                        .ingressHost(kubePathCluster.getIngressHost())
                        .flinkHomeOptions(
                            FlinkHomeOptions
                                .builder()
                                .flinkPath(flinkVersion.getFlinkPath())
                                .realVersion(flinkVersion.getRealVersion())
                                .build()
                        )
                        .build();
                return Optional.of(flinkStopRequest);

        }
        return Optional.empty();
    }

    @Transactional
    @OperateTypeAnnotation(value=OperationsEnum.TRIGGER, jobId="#{jobRto}")
    public synchronized Result<Object> triggerSavepoint(JobRto jobRto) {
        Result<Object> result = new Result<>();
        Job job = this.getMapper().selectById(jobRto.getId());
        String savepointName = jobRto.getSavepointName();
        if (StringUtils.isBlank(savepointName)) {
            putMsg(result, Status.SAVEPOINT_NAME_NULL_ERROR);
            return result;
        }
        // 默认的位置
        String savepointPath = String.format(SAVEPOINT_DIR_FORMAT,
            minio.getDefaultFs(),
            minio.getBucketName(),
            TaskPathResolver.getJobDefaultSavePointDir(job.getProjectId(),
                job.getId(),
                job.getRunJobId()));
        Optional<FlinkTriggerRequest> triggerSavepointOpt = createTriggerSavepoint(job, savepointPath);
        TriggerResponse triggerResponse = triggerSavepointOpt.map(
            triggerSavepoint -> {
                TaskTriggerResponseCommand taskStopResponseCommand =
                    (TaskTriggerResponseCommand) nettyClientService.sendCommand(
                            ClusterType.KUBERNETES,
                        new TaskTriggerCommand(
                            triggerSavepoint,
                            triggerSavepoint
                                .getRunModeType()
                                .getClusterType()
                                .getValue(),
                            MDC.get(Constants.TRACE_ID)
                        ),
                        TaskTriggerResponseCommand.class);
                if (Asserts.isNull(taskStopResponseCommand)) {
                    return null;
                }
                return taskStopResponseCommand.getTriggerResponse();
            }
        ).orElse(null);

        if (Asserts.isNull(triggerResponse) || ResponseStatus.FAIL.equals(triggerResponse.getResponseStatus())) {
            putMsg(result, Status.TRIGGER_FLINK_TASK_INSTANCE_ERROR);
            return result;
        }
        JobSavepoint jobSavepoint = new JobSavepoint();
        jobSavepoint.setJobId(job.getId());
        jobSavepoint.setSavepointPath(triggerResponse.getSavePointAddress());
        jobSavepoint.setSavepointName(jobRto.getSavepointName());
        savePointServiceImpl.insert(jobSavepoint);
        return result.ok();
    }

    private Optional<FlinkTriggerRequest> createTriggerSavepoint(Job job, String savepointAddress) {
        RunModeType runModeType = RunModeType.of(job.getRunModeType());
        // flink 版本
        FlinkVersion flinkVersion = flinkVersionServiceImpl.getMapper().selectById(job.getMotorVersionId());
        // flink 集群
        Cluster cluster = clusterServiceImpl.getMapper().selectById(job.getClusterId());
        String clusterParams = cluster.getClusterParams();
        switch (runModeType) {
            case LOCAL:
            case REMOTE:
            case YARN_APPLICATION:
            case YARN_SESSION:
            case K8S_SESSION:
                return Optional.empty();
            case K8S_APPLICATION:
                K8sClusterParams kubePathCluster = JSONUtils.parseObject(clusterParams, K8sClusterParams.class);
                if (Asserts.isNull(kubePathCluster)) {
                    return Optional.empty();
                }
                FlinkTriggerRequest flinkTriggerRequest = FlinkTriggerRequest.builder()
                    .runJobId(job.getRunJobId())
                    .jobId(job.getId())
                    .clusterId(job.getJobName())
                    .flinkHomeOptions(
                        FlinkHomeOptions.builder()
                            .flinkPath(flinkVersion.getFlinkPath())
                            .realVersion(flinkVersion.getRealVersion())
                           .build()
                    )
                    .ingressHost(kubePathCluster.getIngressHost())
                    .runModeType(runModeType)
                    .savePointAddress(savepointAddress)
                    .nameSpace(kubePathCluster.getNameSpace())
                    .build();
                return Optional.of(flinkTriggerRequest);
        }
        return Optional.empty();
    }

    /**
     * 更新任务运行参数
     * runtimeOptions: this.runtimeOptions,
     * motorVersionId: this.motorVersion.motorId,
     * appArgs: this.appArgs,
     * otherRuntimeConfig: this.flinkYaml,
     * clusterId: this.runClusterInfo.clusterId,
     * fileType: this.fileType,
     * imageId: this.runImageInfo.imageId,
     * id: this.jobId,
     * runModeType: this.runModeType,
     * clusterType: this.runClusterInfo.clusterType,
     */
    @Transactional
    public Result<Object> updateRunTimeConfig(String runtimeOptions,
                                              long motorVersionId,
                                              String appArgs,
                                              String otherRuntimeConfig,
                                              long clusterId,
                                              String fileType,
                                              long imageId,
                                              long jobId,
                                              String runMode,
                                              String clusterType) {

        RunModeType runModeType = RunModeType.parseRunModeType(
            ClusterType.of(clusterType),
            RunMode.of(runMode));
        Result<Object> result = new Result<>();
        // 使用jobId 查询
        Job newJob = this.getMapper().selectById(jobId);
        newJob.setOtherRuntimeConfig(DeflaterUtils.zipString(otherRuntimeConfig));
        newJob.setRuntimeOptions(runtimeOptions);
        newJob.setMotorVersionId(motorVersionId);
        newJob.setAppArgs(appArgs);
        newJob.setClusterId(clusterId);
        newJob.setFileType(fileType);
        newJob.setImageId(imageId);
        newJob.setRunModeType(runModeType.name());
        return result.setData(update(newJob)).ok();
    }

}


