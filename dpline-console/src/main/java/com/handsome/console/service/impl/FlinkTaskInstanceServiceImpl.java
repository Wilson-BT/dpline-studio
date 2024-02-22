package com.handsome.console.service.impl;

import com.handsome.common.Constants;
import com.handsome.common.enums.*;
import com.handsome.common.options.*;
import com.handsome.common.request.*;
import com.handsome.common.util.*;
import com.handsome.console.exception.ServiceException;
import com.handsome.console.handler.DeployHandler;
import com.handsome.console.handler.TaskOperatorHandlerFactory;
import com.handsome.console.service.FlinkTaskInstanceService;
import com.handsome.console.service.NettyClientService;
import com.handsome.console.service.ProjectService;
import com.handsome.dao.dto.FlinkTaskInstanceConfigDto;
import com.handsome.dao.entity.*;
import com.handsome.dao.entity.FlinkVersion;
import com.handsome.dao.mapper.*;
import com.handsome.remote.command.*;
import org.apache.flink.api.common.JobID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static com.handsome.common.util.MinioUtils.addCheckPointAddressPrefix;


@Service
public class FlinkTaskInstanceServiceImpl extends BaseServiceImpl implements FlinkTaskInstanceService {

    private Logger logger = LoggerFactory.getLogger(FlinkTaskInstanceServiceImpl.class);

    @Autowired
    FlinkTaskDefinitionMapper flinkTaskDefinitionMapper;

    @Autowired
    FlinkTagTaskResRelationMapper flinkTagTaskResRelationMapper;

    @Autowired
    FlinkTagTaskUdfRelationMapper flinkTagTaskUdfRelationMapper;

    @Autowired
    FlinkTaskTagLogMapper flinkTaskTagLogMapper;

    @Autowired
    FlinkTaskInstanceMapper flinkTaskInstanceMapper;

    @Autowired
    FlinkVersionMapper flinkVersionMapper;

    @Autowired
    ProjectService projectService;

    @Autowired
    TaskSavepointMapper taskSavepointMapper;

    @Autowired
    NettyClientService nettyClientService;

    MinioUtils minio = MinioUtils.getInstance();

    TaskOperatorHandlerFactory taskHandlerFactory = new TaskOperatorHandlerFactory();

    private static final Pattern REGEX_USER_NAME = Pattern.compile("^[a-zA-Z0-9._-]{3,39}$");


    @Override
    public Result<Object> createTaskInstance(User loginUser, long flinkTaskTagId, String flinkInstanceName, int imageId) {
        // if exist, return ,need delete and recreate
        Result<Object> result = new Result<>();
        FlinkTaskTagLog flinkTaskTagLog = flinkTaskTagLogMapper.selectById(flinkTaskTagId);
        // TODO add user privilege
        if (existFlinkTaskOnSameInstance(flinkTaskTagId, flinkInstanceName)) {
            putMsg(result, Status.FLINK_TASK_INSTANCE_EXIST_ERROR);
            return result;
        }
        // createTaskInstance
        FlinkRunTaskInstance flinkRunTaskInstance = new FlinkRunTaskInstance();
        try {
            Date now = new Date();
            flinkRunTaskInstance.setId(CodeGenerateUtils.getInstance().genCode());
            flinkRunTaskInstance.setFlinkTaskTagId(flinkTaskTagId);
            flinkRunTaskInstance.setFlinkTaskInstanceName(flinkInstanceName);
            flinkRunTaskInstance.setProjectCode(flinkTaskTagLog.getProjectCode());
            flinkRunTaskInstance.setFlinkImageId(imageId);
            flinkRunTaskInstance.setCreateTime(now);
            flinkRunTaskInstance.setUpdateTime(now);
            flinkRunTaskInstance.setUserId(loginUser.getId());
            flinkTaskInstanceMapper.insert(flinkRunTaskInstance);
            putMsg(result, Status.SUCCESS);
            result.setData(flinkRunTaskInstance);
        } catch (CodeGenerateUtils.CodeGenerateException e) {
            logger.error("create task instance error,{}", e.getMessage());
            throw new ServiceException(e.getMessage());
        }
        return result;
    }

    /**
     * update flink task run config
     *
     * @param flinkRunTaskInstanceDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Object> updateTaskInstanceRunConfig(User user, FlinkTaskInstanceConfigDto flinkRunTaskInstanceDto) {
        // TODO 查看用户有没有权限
        Result<Object> result = new Result<>();
        FlinkRunTaskInstance flinkRunTaskInstance = flinkTaskInstanceMapper.selectById(flinkRunTaskInstanceDto.getId());
        if (flinkRunTaskInstance == null) {
            putMsg(result, Status.FLINK_TASK_INSTANCE_NOT_EXIST_ERROR);
            return result;
        }
        // 任务正在运行中，不能更改配置
        if (Asserts.isNotNull(flinkRunTaskInstance.getExecStatus()) && !flinkRunTaskInstance.getExecStatus().isStopped()) {
            putMsg(result, Status.FLINK_TASK_INSTANCE_IS_RUNNING);
            return result;
        }
        try {
            FlinkRunTaskInstance flinkTaskInstance = flinkRunTaskInstanceDto.updateFlinkTaskInstance(flinkRunTaskInstance);
            flinkTaskInstanceMapper.updateById(flinkTaskInstance);
            putMsg(result, Status.SUCCESS);
            result.setData(flinkTaskInstance);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
        return result;
    }

    /**
     * only support k8s session mode and k8s application mode
     * others will implement
     *
     * @param loginUser
     * @param taskInstanceId
     * @return
     */
    @Override
    public Result<Object> deployTaskInstance(User loginUser, long taskInstanceId) {
        Result<Object> result = new Result<>();
        FlinkRunTaskInstance flinkRunTaskInstance = flinkTaskInstanceMapper.selectApplicationInfoById(taskInstanceId);
        if (flinkRunTaskInstance == null) {
            putMsg(result, Status.FLINK_TASK_INSTANCE_NOT_EXIST_ERROR);
            return result;
        }

        FlinkTaskTagLog flinkTaskTagLog = flinkTaskTagLogMapper.selectById(flinkRunTaskInstance.getFlinkTaskTagId());
        DeployHandler deployHandler = taskHandlerFactory.getDeployHandler(flinkTaskTagLog.getTaskType(),
            flinkRunTaskInstance.getRunMode());
        try {
            // 1.compile, create tmp file and directory, create pod templete file
            // sql Mode have udf/connector and other jars,like mysql and so on. All of them called extended-jars
            // custom code only have main jars
            // 2. deploy file
            String deployAddress = deployHandler.deploy(flinkTaskTagLog, flinkRunTaskInstance);
            // 3.update table to deployed and save deploy-address
            flinkRunTaskInstance.setDeployed(Flag.YES);
            // if is session mode,the deployAddress is jarId
            flinkRunTaskInstance.setDeployAddress(deployAddress);
            flinkRunTaskInstance.setUpdateTime(new Date());
            flinkTaskInstanceMapper.updateById(flinkRunTaskInstance);
        } catch (Exception e) {
            logger.error("Deploy flink task instance failed.");
            e.printStackTrace();
            putMsg(result, Status.DEPLOY_FLINK_TASK_INSTANCE_ERROR);
        } finally {
            // 5.delete local directory，exclude pod file
            deployHandler.clear(flinkRunTaskInstance);
            //  delete local tmp file and directory
        }
        // return
        putMsg(result, Status.SUCCESS);
        result.setData(flinkRunTaskInstance);
        return result;
    }

    /**
     * can only run after deployed
     *
     * @param loginUser
     * @param taskInstanceId
     * @param checkpointStartType
     * @param savePointAddress
     * @return
     */
    @Override
    public Map<String, Object> runTaskInstance(User loginUser,
                                               long taskInstanceId,
                                               CheckpointStartType checkpointStartType,
                                               Long savePointId,
                                               String savePointAddress) {
        FlinkRunTaskInstance flinkRunTaskInstance = flinkTaskInstanceMapper.selectApplicationInfoById(taskInstanceId);
        long projectCode = flinkRunTaskInstance.getProjectCode();
        Map<String, Object> result = projectService.queryByCode(loginUser, projectCode);
        if (!result.get(Constants.STATUS).equals(Status.SUCCESS)) {
            return result;
        }
        if (flinkRunTaskInstance.getExecStatus() != null && !flinkRunTaskInstance.getExecStatus().isStopped()) {
            putMsg(result, Status.FLINK_TASK_INSTANCE_IS_RUNNING);
            return result;
        }

        if (flinkRunTaskInstance.getDeployed().equals(Flag.NO)) {
            putMsg(result, Status.FLINK_TASK_NOT_DEPLOY_ERROR);
            return result;
        }

        FlinkTaskTagLog flinkTaskTagLog = flinkTaskTagLogMapper.selectById(flinkRunTaskInstance.getFlinkTaskTagId());
        // 设置为正在提交
        try {
            // 数据库记录改为正在提交
            flinkRunTaskInstance.setCheckpointType(checkpointStartType);
            flinkRunTaskInstance.setExecStatus(ExecStatus.SUBMITTING);
            flinkTaskInstanceMapper.updateById(flinkRunTaskInstance);

            Optional<String> optionalS = inferCheckPointAddress(flinkRunTaskInstance, checkpointStartType, savePointId, savePointAddress);

            SubmitRequest submitRequest = createSubmitRequest(flinkRunTaskInstance, flinkTaskTagLog, optionalS.orElse(null));

            TaskRunResponseCommand taskRunResponseCommand =
                (TaskRunResponseCommand) nettyClientService.sendCommand(
                    new TaskRunCommand(submitRequest),
                    TaskRunResponseCommand.class);
            SubmitResponse submitResponse = taskRunResponseCommand.getSubmitRequest();
            if(submitResponse.getResponseStatus().equals(ResponseStatus.FAIL)){
                putMsg(result,Status.RUN_FLINK_TASK_INSTANCE_ERROR);
                return result;
            }
            // submitResponse
            flinkRunTaskInstance.setRestUrl(submitResponse.getRestUrl());
            result.put(Constants.DATA_LIST, flinkRunTaskInstance);
            return result;
        } catch (Throwable ex) {
            flinkRunTaskInstance.setExecStatus(ExecStatus.FAILED);
            flinkTaskInstanceMapper.updateById(flinkRunTaskInstance);
            putMsg(result, Status.RUN_FLINK_TASK_INSTANCE_ERROR);
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 调用接口，提交任务，之后需要回调 watcher的接口，并做好监控
     *
     * @param flinkRunTaskInstance
     * @param flinkTaskTagLog
     * @param checkPointAddress
     * @return
     */
    private SubmitRequest createSubmitRequest(FlinkRunTaskInstance flinkRunTaskInstance,
                                              FlinkTaskTagLog flinkTaskTagLog,
                                              String checkPointAddress) {
        FlinkVersion flinkVersion = flinkVersionMapper.selectById(flinkTaskTagLog.getFlinkVersionId());

        // task definition
        TaskDefinitionOptions taskDefinitionOptions = TaskDefinitionOptions.builder()
            .taskId(flinkRunTaskInstance.getId())
            .appArgs(flinkTaskTagLog.getClassParams())
            .jarPath(flinkTaskTagLog.getMainJarPath(), flinkRunTaskInstance.getRunMode())
            .dependenceResourceIds(null)
            .mainClass(flinkTaskTagLog.getMainClassName())
            .taskType(flinkTaskTagLog.getTaskType())
            .build();
        // if checkpoint dir is not set, need new a
        CheckpointOptions checkpointOptions = JSONUtils.parseObject(flinkRunTaskInstance.getCheckpointOptions(), CheckpointOptions.class);
        if (Asserts.isNull(checkpointOptions)) {
            checkpointOptions = new CheckpointOptions();
        }
        // default use s3 special path   s3://bucket/project/clusterId/taskPipelineId
        if (StringUtils.isEmpty(checkpointOptions.getStateCheckpointsDir())) {
            checkpointOptions.setStateCheckpointsDir(
                MinioUtils.getMinoCheckPointPathWithBucket(
                    String.valueOf(flinkRunTaskInstance.getProjectCode()),
                    flinkRunTaskInstance.getRunMode().equals(RunModeType.K8S_SESSION)
                        ? flinkRunTaskInstance.getFlinkSessionName()
                        : flinkRunTaskInstance.getFlinkTaskInstanceName(),
                    taskDefinitionOptions.getJobId()
                    )
            );
        }
        checkpointOptions.setFromCheckpointAddress(!flinkRunTaskInstance.getCheckpointType().equals(CheckpointStartType.START_NO_CHECKPOINT));
        checkpointOptions.setCheckpointAddress(checkPointAddress);
        // k8s options
        K8sOptions k8sOptions = K8sOptions.builder()
            .nameSpace(flinkRunTaskInstance.getNameSpace())
            .clusterId(flinkRunTaskInstance.getFlinkTaskInstanceName())
            .serviceAccount(flinkRunTaskInstance.getServiceAccount())
            .sessionName(flinkRunTaskInstance.getFlinkSessionName())
            .kubePath(flinkRunTaskInstance.getKubePath())
            .exposedType(flinkRunTaskInstance.getExposedType())
            .imageAddress(flinkRunTaskInstance.getImageFullName())
            .selectNode(JSONUtils.toMap(flinkRunTaskInstance.getSelectorLables()))
            .build();
        // task other options
        OtherOptions otherOptions = OtherOptions.builder()
            .openChain(flinkRunTaskInstance.getOpenChain())
            .runModeType(flinkRunTaskInstance.getRunMode())
            .resolveOrder(flinkRunTaskInstance.getResolveOrder())
            .deployAddress(flinkRunTaskInstance.getDeployAddress())
            .build();

        return SubmitRequest.builder()
            .resourceOptions(JSONUtils.parseObject(flinkRunTaskInstance.getResourceOptions(), ResourceOptions.class))
            .flinkTaskInstanceName(flinkRunTaskInstance.getFlinkTaskInstanceName())
            .flinkHomeOptions(new FlinkHomeOptions(flinkVersion.getFlinkPath(), flinkVersion.getRealVersion()))
            .checkpointOptions(checkpointOptions)
            .restartOptions(JSONUtils.parseObject(flinkRunTaskInstance.getRestartOptions(), RestartOptions.class))
            .k8sOptions(k8sOptions)
            .otherOptions(otherOptions)
            .taskDefinitionOptions(taskDefinitionOptions)
            .build();
    }

    /**
     * 推测上一次checkpoint的地址
     *
     * @param flinkRunTaskInstance
     * @param checkpointStartType
     * @param savePointId
     * @param savePointAddress
     * @return
     */
    private Optional<String> inferCheckPointAddress(FlinkRunTaskInstance flinkRunTaskInstance, CheckpointStartType checkpointStartType, Long savePointId, String savePointAddress) {
        switch (checkpointStartType) {
            case FROM_SPEC_SAVEPOINT:
                return Optional.ofNullable(addCheckPointAddressPrefix(savePointAddress));
            case START_NO_CHECKPOINT:
                return Optional.empty();
            case FROM_LAST_CHECKPOINT:
//                String checkpointAddress = flinkRunTaskInstance.getCheckpointAddress();
                try {
//                    if (StringUtils.isNotEmpty(checkpointAddress) &&
//                        minio.exists(formatCheckPointAddress(checkpointAddress))) {
//                        return Optional.of(checkpointAddress);
//                    }
                    if (flinkRunTaskInstance.getRunMode().equals(RunModeType.K8S_APPLICATION)) {
                        return Optional.of(
                            minio.getLastCheckPointAddress(
                                flinkRunTaskInstance.getProjectCode(),
                                flinkRunTaskInstance.getFlinkTaskInstanceName(),
                                flinkRunTaskInstance.getJobId()));
                    }
                } catch (IOException e) {
                    logger.warn("Can't find checkpoint address from database or from minio");
                    e.printStackTrace();
                }
                break;
            case FROM_EXISTS_SAVEPOINT:
                // if from exist savepoint
                TaskSavePoint taskSavePoint = taskSavepointMapper.selectById(savePointId);
                if (Asserts.isNotNull(taskSavePoint)) {
                    return Optional.of(taskSavePoint.getSavePointAddress());
                }
        }
        return Optional.empty();
    }

    /**
     * stop task
     *
     * @param loginUser
     * @param taskInstanceId
     * @param savePointAddress
     * @return
     */
    @Override
    public Map<String, Object> stopTaskInstance(User loginUser, long taskInstanceId, boolean withSavePointAddress, String savePointAddress) {
        FlinkRunTaskInstance flinkRunTaskInstance = flinkTaskInstanceMapper.selectApplicationInfoById(taskInstanceId);
        long projectCode = flinkRunTaskInstance.getProjectCode();
        Map<String, Object> result = projectService.queryByCode(loginUser, projectCode);
        if (!result.get(Constants.STATUS).equals(Status.SUCCESS)) {
            return result;
        }
        if (Asserts.isNull(flinkRunTaskInstance.getExecStatus()) || flinkRunTaskInstance.getExecStatus().isStopped()) {
            putMsg(result, Status.FLINK_TASK_INSTANCE_IS_STOPPED);
            return result;
        }
        try {
            // update db to cancelling
            flinkRunTaskInstance.setExecStatus(ExecStatus.CANCELLING);
            flinkTaskInstanceMapper.updateById(flinkRunTaskInstance);
            StopRequest stopRequest = createStopRequest(flinkRunTaskInstance, withSavePointAddress, savePointAddress);
            TaskStopResponseCommand taskStopResponseCommand = (TaskStopResponseCommand) nettyClientService.sendCommand(
                new TaskStopCommand(stopRequest),
                TaskStopResponseCommand.class);
            StopResponse stopResponse = taskStopResponseCommand.getStopResponse();
            // 然后把 savepoint 地址保存到 savepoint 表中
            if (!stopResponse.getResponseStatus().equals(ResponseStatus.SUCCESS)) {
                putMsg(result, Status.STOP_FLINK_TASK_INSTANCE_ERROR);
                return result;
            }
            if(stopRequest.getWithSavePointAddress()){
                TaskSavePoint taskSavePoint = TaskSavePoint.builder().id(CodeGenerateUtils.getInstance().genCode())
                    .taskId(stopRequest.getTaskId())
                    .savePointAddress(stopRequest.getSavePointAddress())
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
                taskSavepointMapper.insert(taskSavePoint);
                logger.info("Savepoint address [{}] has been inert into database.", taskSavePoint.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 为某个任务手动创建 savepoint 地址
     *
     * @param loginUser
     * @param taskInstanceId
     * @param withSavePointAddress
     * @param savePointAddress
     * @return
     */
    @Override
    public Map<String, Object> triggerSavePoint(User loginUser, long taskInstanceId, boolean withSavePointAddress, String savePointAddress) {
        // 如果应用不是在运行状态，不可以使用
        Map<String, Object> result;
        FlinkRunTaskInstance flinkRunTaskInstance = flinkTaskInstanceMapper.selectById(taskInstanceId);
        if (Asserts.isNull(flinkRunTaskInstance)) {
            result = new HashMap<>();
            putMsg(result, Status.FLINK_TASK_INSTANCE_NOT_EXIST_ERROR);
            return result;
        }
        result = projectService.queryByCode(loginUser, flinkRunTaskInstance.getProjectCode());
        if (!result.get(Constants.STATUS).equals(Status.SUCCESS)) {
            return result;
        }
        if (Asserts.isNull(flinkRunTaskInstance.getExecStatus()) || flinkRunTaskInstance.getExecStatus().isStopped()) {
            putMsg(result, Status.FLINK_TASK_INSTANCE_IS_STOPPED);
            return result;
        }
        TriggerRequest triggerRequest = createTriggerRequest(flinkRunTaskInstance, withSavePointAddress, savePointAddress);
        TaskTriggerResponseCommand taskStopResponseCommand = (TaskTriggerResponseCommand) nettyClientService.sendCommand(
            new TaskTriggerCommand(triggerRequest),
            TaskTriggerResponseCommand.class);
        // 返回的
        TriggerResponse triggerResponse = taskStopResponseCommand.getTriggerResponse();
        if (triggerResponse.getResponseStatus().equals(ResponseStatus.SUCCESS)) {
            // 然后把 savepoint 地址保存到 savepoint 表中
            TaskSavePoint taskSavePoint = null;
            try {
                taskSavePoint = TaskSavePoint.builder().id(CodeGenerateUtils.getInstance().genCode())
                    .taskId(triggerResponse.getTaskId())
                    .savePointAddress(triggerResponse.getSavePointAddress())
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            } catch (CodeGenerateUtils.CodeGenerateException e) {
                e.printStackTrace();
            }
            taskSavepointMapper.insert(taskSavePoint);
            logger.info("Savepoint address [{}] has been inert into database.", taskSavePoint.toString());
        }

        return result;
    }

    /**
     * create triggerRequest
     *
     * @param flinkRunTaskInstance
     * @param withSavePointAddress
     * @param savePointAddress
     * @return
     */
    private TriggerRequest createTriggerRequest(FlinkRunTaskInstance flinkRunTaskInstance, boolean withSavePointAddress, String savePointAddress) {
        TriggerRequest triggerRequest = new TriggerRequest();
        triggerRequest.setRunModeType(flinkRunTaskInstance.getRunMode());
        triggerRequest.setSavePointAddress(savePointAddress);
        triggerRequest.setWithSavePointAddress(withSavePointAddress);
        triggerRequest.setJobId(flinkRunTaskInstance.getJobId());
        triggerRequest.setTaskId(flinkRunTaskInstance.getId());
        // application 模式需要使用
        switch (flinkRunTaskInstance.getRunMode()) {
            case K8S_APPLICATION:
                triggerRequest.setClusterId(flinkRunTaskInstance.getFlinkTaskInstanceName());
                break;
            case K8S_SESSION:
                triggerRequest.setClusterId(flinkRunTaskInstance.getFlinkSessionName());
                break;
            case REMOTE:
                break;
        }
        return triggerRequest;
    }

    private StopRequest createStopRequest(FlinkRunTaskInstance flinkRunTaskInstance, boolean withSavePointAddress, String savePointAddress) {
        StopRequest stopRequest = new StopRequest();
        stopRequest.setRunModeType(flinkRunTaskInstance.getRunMode());
        stopRequest.setSavePointAddress(savePointAddress);
        // 需要添加 kubePath 和 nameSpace，方便添加 ingress 修改
        stopRequest.setNameSpace(flinkRunTaskInstance.getNameSpace());
        stopRequest.setKubePath(flinkRunTaskInstance.getKubePath());
        stopRequest.setWithSavePointAddress(withSavePointAddress);
        stopRequest.setJobId(flinkRunTaskInstance.getJobId());
        stopRequest.setTaskId(flinkRunTaskInstance.getId());
        // application 模式需要使用
        switch (flinkRunTaskInstance.getRunMode()) {
            case K8S_APPLICATION:
                stopRequest.setClusterId(flinkRunTaskInstance.getFlinkTaskInstanceName());
                break;
            case K8S_SESSION:
                stopRequest.setClusterId(flinkRunTaskInstance.getFlinkSessionName());
                break;
            case REMOTE:
                break;
        }
        return stopRequest;
    }


    @Override
    public Result<Object> listAllTaskInstanceStatus(User loginUser) {

        return null;
    }

    Boolean existFlinkTaskOnSameInstance(long flinkTaskTagId, String flinkInstanceName) {
        return flinkTaskInstanceMapper.existFlinkTaskOnSameInstance(flinkTaskTagId, flinkInstanceName) == Boolean.TRUE;
    }


}




