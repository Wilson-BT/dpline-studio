package com.dpline.console.service.impl;

import com.dpline.common.enums.*;
import com.dpline.common.params.JobConfig;
import com.dpline.common.params.K8sClusterParams;
import com.dpline.common.util.*;
import com.dpline.console.service.ClusterService;
import com.dpline.console.service.GenericService;
import com.dpline.console.service.NettyClientService;
import com.dpline.console.util.ContextUtils;
import com.dpline.dao.entity.Cluster;
import com.dpline.dao.entity.ClusterUser;
import com.dpline.dao.entity.Job;
import com.dpline.dao.entity.User;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.mapper.ClusterMapper;
import com.dpline.dao.rto.ClusterRto;
import com.dpline.remote.command.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClusterServiceImpl extends GenericService<Cluster, Long> implements ClusterService {

    private static final Logger logger = LoggerFactory.getLogger(ClusterServiceImpl.class);

    public ClusterServiceImpl(@Autowired ClusterMapper clusterMapper) {
        super(clusterMapper);
    }

    public ClusterMapper getMapper() {
        return (ClusterMapper) super.genericMapper;
    }

    @Autowired
    ClusterUserServiceImpl clusterUserServiceImpl;

    @Autowired
    NettyClientService nettyClientService;

    @Autowired
    JobServiceImpl jobServiceImpl;


    @Override
    @Transactional
    public Result<Object> create(Cluster cluster) {
        Result<Object> result = new Result<>();
        if(!isAdmin(ContextUtils.get().getUser())){
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }
        // 如果已经存在相同名字，则退出
        if (existSameName(cluster.getClusterName())) {
            putMsg(result, Status.CLUSTER_SAME_NAME_EXIST);
            return result;
        }

        if (existSameClusterParams(cluster.getClusterParams())) {
            putMsg(result, Status.SAME_CLUSTER_EXIST);
            return result;
        }
        try {
            cluster.setId(CodeGenerateUtils.getInstance().genCode());
            K8sClientAddCommand k8sClientAddCommand = new K8sClientAddCommand(cluster.getId(),cluster.getClusterParams());
            K8sClientAddResponseCommand k8sClientAddResponseCommand = (K8sClientAddResponseCommand) nettyClientService.sendCommand(k8sClientAddCommand, K8sClientAddResponseCommand.class);
            if (Asserts.isNull(k8sClientAddResponseCommand)
                    || k8sClientAddResponseCommand.getK8sClusterResponse().getResponseStatus().equals(ResponseStatus.FAIL)) {
                putMsg(result, Status.CLUSTER_CREATE_ERROR);
                return result;
            }
            insert(cluster);
            return result.ok();
        } catch (CodeGenerateUtils.CodeGenerateException e) {
            logger.info(ExceptionUtil.exceptionToString(e));
            putMsg(result, Status.CLUSTER_CREATE_ERROR);
            return result;
        }
    }

    private boolean existSameClusterParams(String clusterParams) {
        List<Cluster> clusters = this.getMapper().selectByClusterParams(clusterParams);
        return CollectionUtils.isNotEmpty(clusters);
    }

    private boolean existSameName(String clusterName) {
        List<Cluster> clusters = this.getMapper().selectByClusterName(clusterName);
        return CollectionUtils.isNotEmpty(clusters);
    }

    @Override
    @Transactional
    public Result<Object> delete(Cluster cluster) {
        Result<Object> result = new Result<>();
        if(!isAdmin(ContextUtils.get().getUser())){
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }
        List<Job> jobList = jobServiceImpl.getMapper().selectByClusterId(cluster.getId());
        if(CollectionUtils.isNotEmpty(jobList)){
            putMsg(result,Status.CLUSTER_BOUNDED_JOB_EXIST,jobList.get(0).getJobName());
            return result;
        }
        ClusterUser clusterUser = new ClusterUser();
        clusterUser.setClusterId(cluster.getId());
        List<Job> jobs = jobServiceImpl.selectByClusterId(cluster.getId());
        if (CollectionUtils.isNotEmpty(jobs)) {
            putMsg(result, Status.CLUSTER_BOUNDED_JOB_EXIST,jobs.get(0).getJobName());
            return result;
        }
        K8sClientDelCommand k8sClientDelCommand = new K8sClientDelCommand(cluster.getId());
        K8sClientDelResponseCommand k8sClientDelResponseCommand =
            (K8sClientDelResponseCommand) nettyClientService.sendCommand(k8sClientDelCommand, K8sClientDelResponseCommand.class);
        if (Asserts.isNull(k8sClientDelResponseCommand)) {
            putMsg(result, Status.CLUSTER_DELETE_ERROR);
            return result;
        }
        if (k8sClientDelResponseCommand.getK8sClusterResponse().getResponseStatus().equals(ResponseStatus.FAIL)) {
            putMsg(result, Status.CLUSTER_DELETE_ERROR);
            return result;
        }
        // 先删除关系
        clusterUserServiceImpl.getMapper().deleteRelationByClusterId(cluster.getId());
        // 然后删除实体信息
        return result.setData(delete(cluster.getId())).ok();
    }

    @Override
    public Result<Object> updateState(Cluster cluster) {
        Result<Object> result = new Result<>();
        if(!isAdmin(ContextUtils.get().getUser())){
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }
        if (cluster == null || cluster.getId() == null || cluster.getEnabledFlag() == null) {
            putMsg(result,Status.CLUSTER_CREATE_ERROR);
            return result;
        }
        boolean isSuccess;

        Cluster oldCluster = this.getMapper().selectById(cluster.getId());
        if (Flag.YES.getCode() == cluster.getEnabledFlag()) {
            isSuccess = remoteAddK8sClient(oldCluster);
        } else {
            // 如果停用的话，需要sendDel
            // 如果有依赖这个cluster的job，直接置为失败
            List<Job> jobList = jobServiceImpl.getMapper().selectByClusterId(cluster.getId());
            if(CollectionUtils.isNotEmpty(jobList)){
                putMsg(result,Status.CLUSTER_BOUNDED_JOB_EXIST,jobList.get(0).getJobName());
                return result;
            }
            isSuccess = remoteDeleteK8sClient(cluster.getId());
        }
        if (isSuccess){
            oldCluster.setId(cluster.getId());
            oldCluster.setEnabledFlag(cluster.getEnabledFlag());
            return result.setData(update(oldCluster)).ok();
        }
        putMsg(result,Status.CLUSTER_CREATE_ERROR);
        return result;
    }

    private boolean remoteAddK8sClient(Cluster cluster){
        K8sClientAddCommand k8sClientAddCommand = new K8sClientAddCommand(cluster.getId(),cluster.getClusterParams());
        K8sClientAddResponseCommand k8sClientAddResponseCommand = (K8sClientAddResponseCommand) nettyClientService.sendCommand(k8sClientAddCommand, K8sClientAddResponseCommand.class);
        if(Asserts.isNull(k8sClientAddResponseCommand) ||
            k8sClientAddResponseCommand.getK8sClusterResponse().getResponseStatus().equals(ResponseStatus.FAIL)){
            return false;
        }
        return true;
    }

    private boolean remoteDeleteK8sClient(Long clusterId){
        K8sClientDelCommand k8sClientDelCommand = new K8sClientDelCommand(clusterId);
        K8sClientDelResponseCommand k8sClientDelResponseCommand = (K8sClientDelResponseCommand)nettyClientService.sendCommand(k8sClientDelCommand, K8sClientDelResponseCommand.class);
        if(Asserts.isNull(k8sClientDelResponseCommand) ||
            k8sClientDelResponseCommand.getK8sClusterResponse().getResponseStatus().equals(ResponseStatus.FAIL)){
            return false;
        }
        return true;
    }

    /**
     * 更新k8s 集群配置,同事需要异步更新远端配置
     * @param cluster
     * @return
     */
    @Override
    public Result<Object> updateInfo(Cluster cluster) {
        Result<Object> result = new Result<>();
        putMsg(result,Status.CLUSTER_CREATE_ERROR);
        if (cluster == null) {
            return result;
        }
        // operator perm
        if(!isAdmin(ContextUtils.get().getUser())){
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }


        String clusterParams = cluster.getClusterParams();
        // params can`t be empty
        K8sClusterParams k8sClusterParams = JSONUtils.parseObject(clusterParams, K8sClusterParams.class);
        if(Asserts.isNull(k8sClusterParams)){
            putMsg(result, Status.CLUSTER_UPDATE_ERROR);
            return result;
        }

        if(StringUtils.isEmpty(k8sClusterParams.getNameSpace()) || StringUtils.isEmpty(k8sClusterParams.getKubePath())){
            putMsg(result, Status.CLUSTER_PARAMS_IS_EMPTY);
            return result;
        }
        // check same cluster
        List<Cluster> clusters = this.getMapper().selectByClusterParamsAndId(cluster.getId(), clusterParams);
        if (CollectionUtils.isNotEmpty(clusters)) {
            putMsg(result, Status.SAME_CLUSTER_EXIST);
            return result;
        }
        // if same, then update directly
        Cluster oldCluster = this.getMapper().selectById(cluster.getId());
        if(oldCluster.getClusterParams().equals(clusterParams)){
            int updateCnt = update(cluster);
            return result.setData(updateCnt).ok();
        }

        // if not same，need judge if there are any jobs had bounded by it.
        List<Job> jobList = jobServiceImpl.getMapper().selectByClusterId(cluster.getId());
        if(CollectionUtils.isNotEmpty(jobList)){
            putMsg(result,Status.CLUSTER_BOUNDED_JOB_EXIST,jobList.get(0).getJobName());
            return result;
        }
        // no bounded job, then update and reCreate k8s client
        // if cluster kubePath and namespace is changed, need delete and reCreate new k8s client
        K8sClientUpdateCommand k8sClientUpdateCommand = new K8sClientUpdateCommand(
                cluster.getId(),
                oldCluster.getClusterParams(),
                cluster.getClusterParams());
        K8sClientUpdateResponseCommand k8sClientUpdateResponseCommand = (K8sClientUpdateResponseCommand)nettyClientService.sendCommand(
            k8sClientUpdateCommand, K8sClientUpdateResponseCommand.class);
        if(Asserts.isNull(k8sClientUpdateResponseCommand) ||
                        k8sClientUpdateResponseCommand.getResponseStatus().equals(ResponseStatus.FAIL)){
            result.setMsg(k8sClientUpdateResponseCommand.getMsg());
            return result;
        }
        int updateCnt = update(cluster);
        return result.setData(updateCnt).ok();
    }

    @Override
    public Pagination<Cluster> list(ClusterRto clusterRto) {
        Pagination<Cluster> clusterPagination = Pagination.getInstanceFromRto(clusterRto);
        this.executePagination(x -> this.getMapper().list(x), clusterPagination);
        return clusterPagination;
    }

    @Override
    public Result<Object> selectClusterById(Long clusterId) {
        Result<Object> result = new Result<>();
        // 使用 clusterId 查找
        if(Asserts.isZero(clusterId)){
            putMsg(result,Status.JOB_CLUSTER_NOT_EXIST);
            return result;
        }
        Cluster cluster = this.getMapper().selectById(clusterId);
        // 如果是空，那么直接返回故障
        if(Asserts.isNull(cluster)){
            putMsg(result,Status.JOB_CLUSTER_NOT_EXIST);
            return result;
        }
        JobConfig.RunClusterInfo runClusterInfo = new JobConfig.RunClusterInfo();
        runClusterInfo.setClusterId(clusterId);
        runClusterInfo.setClusterType(cluster.getClusterType());
        runClusterInfo.setClusterName(cluster.getClusterName());
        return result.setData(runClusterInfo).ok();
    }

    /**
     * 根据环境和用户权限，判断引擎
     *
     * @return
     */
    public List<Cluster> getAuthedCluster(User user, String clusterType) {
        // 查看是否是管理员,是管理员则置为null，不是管理员，则置为
        return this.getMapper()
            .queryAuthedCluster(isAdmin(user) ? null : user.getUserCode(),
                clusterType);
    }
}
