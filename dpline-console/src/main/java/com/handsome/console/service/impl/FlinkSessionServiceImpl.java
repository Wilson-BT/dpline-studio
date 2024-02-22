package com.handsome.console.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.handsome.common.enums.Flag;
import com.handsome.common.enums.RunModeType;
import com.handsome.common.enums.Status;
import com.handsome.console.exception.ServiceException;
import com.handsome.console.service.FlinkSessionService;
import com.handsome.common.util.Result;
import com.handsome.dao.entity.FlinkRunTaskInstance;
import com.handsome.dao.entity.FlinkSession;
import com.handsome.dao.entity.User;
import com.handsome.dao.mapper.FlinkSessionMapper;
import com.handsome.dao.mapper.FlinkTaskInstanceMapper;
import com.handsome.dao.mapper.K8sNameSpaceMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FlinkSessionServiceImpl extends BaseServiceImpl implements FlinkSessionService {

    private Logger logger = LoggerFactory.getLogger(FlinkSessionServiceImpl.class);

    @Autowired
    FlinkSessionMapper flinkSessionMapper;

    @Autowired
    K8sNameSpaceMapper k8sNameSpaceMapper;

    @Autowired
    FlinkTaskInstanceMapper flinkTaskInstanceMapper;
    /**
     * create flink session
     *
     * @param loginUser
     * @param flinkSessionName
     * @param k8sNamespaceId
     * @param taskmanagerCpuNum
     * @param taskmanagerMemSize
     * @param taskmanagerSlotNum
     * @param jobmanagerProcessSize
     * @param kubernetesClusterId
     * @return
     */
    @Override
    public Result<Object> createFlinkSessionDefinition(User loginUser,
                                                       String flinkSessionName,
                                                       long k8sNamespaceId,
                                                       int taskmanagerNum,
                                                       int taskmanagerCpuNum,
                                                       int taskmanagerMemSize,
                                                       int taskmanagerSlotNum,
                                                       int jobmanagerProcessSize,
                                                       String kubernetesClusterId) {
        // flink session userId
        Result<Object> result = new Result<>();
        putMsg(result, Status.SUCCESS);
        if (existSameKubernetesClusterId(k8sNamespaceId, kubernetesClusterId)) {
            putMsg(result, Status.FLINK_SESSION_CLUSTER_ID_EXISTS);
            return result;
        }
        //  check k8sNameSpace
        if (existSameFlinkSessionName(loginUser.getId(), flinkSessionName)) {
            putMsg(result, Status.FLINK_SESSION_CLUSTER_NAME_EXIST_ERROR);
            return result;
        }
        //TODO 需要校验 cluster-id的命名规则
        try {
            Date now = new Date();
            FlinkSession flinkSession = new FlinkSession();
            flinkSession.setFlinkSessionName(flinkSessionName);
            flinkSession.setJobmanagerProcessSize(jobmanagerProcessSize);
            flinkSession.setKubernetesClusterId(kubernetesClusterId);
            flinkSession.setTaskmanagerCpuNum(taskmanagerCpuNum);
            flinkSession.setTaskmanagerMemSize(taskmanagerMemSize);
            flinkSession.setTaskmanagerNum(taskmanagerNum);
            flinkSession.setTaskmanagerSlotNum(taskmanagerSlotNum);
            flinkSession.setUseId(loginUser.getId());
            flinkSession.setK8sNamespaceId(k8sNamespaceId);
            flinkSession.setUpdateTime(now);
            flinkSession.setCreateTime(now);
            flinkSessionMapper.insert(flinkSession);
            result.setData(flinkSession);
            return result;
        } catch (Exception e) {
            throw new ServiceException(e.toString());
        }
    }

    @Override
    public Result<Object> updateFlinkSessionDefinition(User loginUser,
                                                       int id,
                                                       String flinkSessionName,
                                                       long k8sNamespaceId,
                                                       int taskmanagerNum,
                                                       int taskmanagerCpuNum,
                                                       int taskmanagerMemSize,
                                                       int taskmanagerSlotNum,
                                                       int jobmanagerProcessSize,
                                                       String kubernetesClusterId) {
        Result<Object> result = new Result<>();
        FlinkSession flinkSession = flinkSessionMapper.selectById(id);
        // running,can't update
        if (flinkSession.getStatus().equals(Flag.YES)) {
            putMsg(result, Status.FLINK_SESSION_CLUSTER_RUNNING);
            return result;
        }
        if (!flinkSession.getFlinkSessionName().equals(flinkSessionName) && existSameFlinkSessionName(loginUser.getId(), flinkSessionName)) {
            putMsg(result, Status.FLINK_SESSION_CLUSTER_NAME_EXIST_ERROR);
            return result;
        }
        if (!flinkSession.getKubernetesClusterId().equals(kubernetesClusterId) && existSameKubernetesClusterId(k8sNamespaceId, kubernetesClusterId)) {
            putMsg(result, Status.FLINK_SESSION_CLUSTER_ID_EXISTS);
            return result;
        }
        FlinkSession newFlinkSession = new FlinkSession();
        Date now = new Date();
        newFlinkSession.setId(flinkSession.getId());
        newFlinkSession.setFlinkSessionName(flinkSessionName);
        newFlinkSession.setJobmanagerProcessSize(jobmanagerProcessSize);
        newFlinkSession.setKubernetesClusterId(kubernetesClusterId);
        newFlinkSession.setTaskmanagerCpuNum(taskmanagerCpuNum);
        newFlinkSession.setTaskmanagerMemSize(taskmanagerMemSize);
        newFlinkSession.setTaskmanagerNum(taskmanagerNum);
        newFlinkSession.setTaskmanagerSlotNum(taskmanagerSlotNum);
        newFlinkSession.setUseId(loginUser.getId());
        newFlinkSession.setK8sNamespaceId(k8sNamespaceId);
        newFlinkSession.setUpdateTime(now);
        flinkSessionMapper.updateById(newFlinkSession);
        result.setData(flinkSession);
        return result;
    }

    /**
     * 开启一个flink session
     *
     * @param id
     * @return
     */
    @Override
    public Result<Object> startFlinkSession(int id) {
        FlinkSession flinkSession = flinkSessionMapper.selectById(id);
        // TODO 如何启动一个 FlinkSession
        return null;
    }

    @Override
    public Result<Object> deleteFlinkSession(int id) {
        Result<Object> result = new Result<>();
        FlinkSession flinkSession = flinkSessionMapper.selectById(id);
        // 判断 flink session 是否在运行
        if (flinkSession.getStatus().equals(Flag.YES)) {
            putMsg(result, Status.FLINK_SESSION_CLUSTER_RUNNING);
            return result;
        }
        try {
            flinkSessionMapper.deleteById(id);
            putMsg(result, Status.SUCCESS);
            return result;
        } catch (Exception e) {
            throw new ServiceException(e.toString());
        }
    }

    /**
     * stop
     *
     * @param id
     * @return
     */
    @Override
    public Result<Object> stopFlinkSession(int id) {
        Result<Object> result = new Result<>();
        FlinkSession flinkSession = flinkSessionMapper.selectById(id);
        // 判断 flink session 是否在运行
        if (!flinkSession.getStatus().equals(Flag.YES)) {
            putMsg(result, Status.FLINK_SESSION_CLUSTER_NOT_RUNNING);
            return result;
        }
        // 如果上面有flink instance，可以停止这个应用
        QueryWrapper<FlinkRunTaskInstance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("run_mode", RunModeType.K8S_SESSION.getKey())
                    .eq("k8s_session_cluster_id", flinkSession.getId());
        List<FlinkRunTaskInstance> flinkRunTaskInstances = flinkTaskInstanceMapper.selectList(queryWrapper);
        Set<FlinkRunTaskInstance> collect = flinkRunTaskInstances.stream()
                .filter(f -> f.getExecStatus().typeIsRunning() || f.getExecStatus().isStopping())
                .collect(Collectors.toSet());
        if(CollectionUtils.isNotEmpty(collect)){
            String flinkTaskInstanceName = flinkRunTaskInstances.get(0).getFlinkTaskInstanceName();
            logger.error("task [{}] has bound it",flinkTaskInstanceName);
            putMsg(result,Status.DELETE_FLINK_SESSION_CLUSTER_RELATED_TASK_EXISTS,flinkTaskInstanceName);
        }
        //TODO remote k8s and Stop flink session
        putMsg(result,Status.SUCCESS);
        return result;
    }

    private Boolean existSameKubernetesClusterId(long k8sNamespaceId,
                                                 String kubernetesClusterId) {
        return flinkSessionMapper.existSameKubernetesClusterId(k8sNamespaceId, kubernetesClusterId) == Boolean.TRUE;
    }

    private Boolean existSameFlinkSessionName(int userId, String flinkSessionName) {
        return flinkSessionMapper.existSameFlinkSessionName(userId, flinkSessionName) == Boolean.TRUE;
    }
}
