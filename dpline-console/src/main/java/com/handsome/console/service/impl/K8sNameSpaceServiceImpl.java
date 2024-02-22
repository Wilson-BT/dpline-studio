package com.handsome.console.service.impl;

import com.handsome.common.K8sClientManager;
import com.handsome.common.enums.ReleaseState;
import com.handsome.common.enums.ResFsType;
import com.handsome.common.enums.Status;
import com.handsome.common.util.CodeGenerateUtils;
import com.handsome.common.util.FileUtils;
import com.handsome.common.util.StringUtils;
import com.handsome.console.exception.ServiceException;
import com.handsome.dao.entity.FlinkSession;
import com.handsome.dao.mapper.FlinkSessionMapper;
import com.handsome.dao.entity.K8sNameSpace;
import com.handsome.dao.entity.User;
import com.handsome.dao.mapper.*;
import com.handsome.console.service.K8sNameSpaceService;
import com.handsome.common.util.Result;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class K8sNameSpaceServiceImpl extends BaseServiceImpl implements K8sNameSpaceService {

    private static final Logger logger = LoggerFactory.getLogger(K8sNameSpaceServiceImpl.class);

    @Autowired
    K8sNameSpaceMapper k8sNameSpaceMapper;

    @Autowired
    K8sNameSpacesUserMapper k8sNameSpacesUserMapper;

    @Autowired
    FlinkTaskInstanceMapper flinkTaskInstanceMapper;

    @Autowired
    FlinkSessionMapper flinkSessionMapper;

    /**
     * create K8sNameSpace
     *
     * @param loginUser     log user
     * @param name          k8s name
     * @param description   k8s desc
     * @param k8sConfigPath kube config path
     * @return result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Object> createK8sNameSpace(User loginUser, String name, String description, String k8sConfigPath, String serviceAccount, String selectorLables) {
        Result<Object> result = new Result<>();
        putMsg(result, Status.SUCCESS);
        if (!isAdmin(loginUser)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }
        // 检查这个configPath 或者namespace名称是否存在
        if (existK8sNameSpace(name, k8sConfigPath)) {
            putMsg(result, Status.K8S_ENVIRIONMENT_NAME_EXISTS, name + " at " + k8sConfigPath);
            return result;
        }
        // 判断 路径是否存在
        try {
            if (!FileUtils.checkFileExist(k8sConfigPath, ResFsType.LOCAL)) {
                putMsg(result, Status.K8S_ENVIRIONMENT_PATH_EXISTS);
                return result;
            }
        } catch (IOException e) {
            logger.error("{}.Error messager:{}", Status.CREATE_K8S_ENVIRIONMENT_ERROR.getMsg(), e.getMessage());
            putMsg(result, Status.CREATE_K8S_ENVIRIONMENT_ERROR, name + " at " + k8sConfigPath);
            return result;
        }
        // 有权限，不存在，直接创建
        K8sNameSpace k8sNameSpace = null;
        try {
            k8sNameSpace = K8sNameSpace.builder()
                    .id(CodeGenerateUtils.getInstance().genCode())
                    .k8sNameSpace(name)
                    .kubePath(k8sConfigPath)
                    .desc(description)
                    .online(ReleaseState.OFFLINE)
                    .serviceAccount(serviceAccount)
                    .selectorLables(selectorLables)
                    .creatTime(new Date())
                    .updateTime(new Date())
                    .build();
        } catch (CodeGenerateUtils.CodeGenerateException e) {
            logger.error("k8s instance create failed.{}", e.getMessage());
            throw new ServiceException(String.format("k8s instance create failed. %s", e.getMessage()));
        }
        k8sNameSpaceMapper.insert(k8sNameSpace);
        // TODO ，需要做到启动的时候就要加载 所有的客户端，或者是监听到后端启动，就启动某个任务,
        KubernetesClient k8sClient = K8sClientManager.createK8sClient(k8sConfigPath,name);
        if (k8sClient == null) {
            putMsg(result, Status.CREATE_K8S_ENVIRIONMENT_ERROR);
        }
        return result;
    }

    /**
     * update k8s name space
     *
     * @param loginUser      login user
     * @param k8sNamespaceId k8s namespace id
     * @param online         release state
     * @param name           name
     * @param description    desc
     * @param k8sConfigPath  kube config path
     * @return
     */
    @Override
    @Transactional
    public Result<Object> updateK8sNameSpace(User loginUser,
                                             long k8sNamespaceId,
                                             ReleaseState online,
                                             String name,
                                             String description,
                                             String k8sConfigPath,
                                             String serviceAccount,
                                             String selectorLables) {
        Result<Object> result = new Result<>();
        putMsg(result, Status.SUCCESS);
        if (!isAdmin(loginUser)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }
        K8sNameSpace k8sNameSpaceOld = k8sNameSpaceMapper.selectById(k8sNamespaceId);
        if (k8sNameSpaceOld == null) {
            putMsg(result, Status.K8S_ENVIRIONMENT_PATH_NOT_EXISTS);
            return result;
        }
        // if name has already existed, then return
        if (!k8sNameSpaceOld.getNameSpace().equals(name) && existK8sNameSpace(name, null)) {
            putMsg(result, Status.K8S_ENVIRIONMENT_NAME_EXISTS);
            return result;
        }
        if (k8sNameSpaceOld.getReleaseState().equals(ReleaseState.ONLINE) && online.equals(ReleaseState.OFFLINE)) {
            Optional<Result<Object>> objectResult = existK8sNameSpaceRelationTask(result, k8sNamespaceId);
            if (objectResult.isPresent()){
                return objectResult.get();
            }
        }
        if (!k8sNameSpaceOld.getKubePath().equals(k8sConfigPath)) {
            if (existK8sNameSpace(null, k8sConfigPath)) {
                // if path has already existed
                putMsg(result, Status.K8S_ENVIRIONMENT_PATH_EXISTS);
                return result;
            }
            // if k8sConfigPath changed，need delete
            K8sClientManager.deleteClientIfExist(k8sNameSpaceOld.getKubePath(),k8sNameSpaceOld.getNameSpace());
        }
        K8sNameSpace k8sNameSpaceNew = K8sNameSpace.builder()
                .k8sNameSpace(name)
                .id(k8sNamespaceId)
                .serviceAccount(serviceAccount)
                .selectorLables(selectorLables)
                .kubePath(k8sConfigPath)
                .online(online)
                .desc(description)
                .updateTime(new Date())
                .build();
        KubernetesClient k8sClient = K8sClientManager.createK8sClient(k8sConfigPath,name);
        k8sNameSpaceMapper.updateById(k8sNameSpaceNew);
        if (k8sClient == null) {
            putMsg(result, Status.UPDATE_K8S_ENVIRIONMENT_ERROR);
        }
        return result;
    }

    /**
     * delete k8s name space
     *
     * @param loginUser
     * @param k8sNameSpaceId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Object> deleteK8sNameSpace(User loginUser, long k8sNameSpaceId) {
        Result<Object> result = new Result<>();
        putMsg(result, Status.SUCCESS);
        if (!isAdmin(loginUser)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }
        // 如果使用flink Id
        K8sNameSpace k8sNameSpace = k8sNameSpaceMapper.selectById(k8sNameSpaceId);
        if (k8sNameSpace == null) {
            putMsg(result, Status.K8S_ENVIRIONMENT_PATH_NOT_EXISTS);
            return result;
        }
        if (k8sNameSpace.getReleaseState().equals(ReleaseState.ONLINE)) {
            putMsg(result, Status.K8S_ENVIRIONMENT_ONLINE_ERROR);
            return result;
        }
        k8sNameSpaceMapper.deleteById(k8sNameSpaceId);
        k8sNameSpacesUserMapper.deleteByk8sNameSpaceId(k8sNameSpaceId);
        K8sClientManager.deleteClientIfExist(k8sNameSpace.getKubePath(),k8sNameSpace.getNameSpace());
        return result;
    }

    /**
     * authoried k8s namespace by user
     *
     * @param loginUser
     * @return
     */
    @Override
    public Result<Object> queryAuthK8sNameSpace(User loginUser) {
        Result<Object> result = new Result<>();
        putMsg(result, Status.SUCCESS);
        Set<K8sNameSpace> k8sNameSpaces = k8sNameSpaceMapper.queryK8sNameSpacesIdListByUserId(isAdmin(loginUser) ? 0 : loginUser.getId(), null);
        result.setData(k8sNameSpaces);
        return result;
    }

    public boolean existK8sNameSpace(String name, String k8sConfigPath) {
        return k8sNameSpaceMapper.existK8sNameSpace(name, k8sConfigPath) == Boolean.TRUE;
    }


    /**
     * session and task instance table has
     *
     * @param result
     * @param k8sNameSpaceId
     * @return
     */
    public Optional<Result<Object>> existK8sNameSpaceRelationTask(Result<Object> result, long k8sNameSpaceId) {
        // flink session dependence
        List<FlinkSession> flinkSessionList = flinkSessionMapper.queryByk8sNameSpaceId(k8sNameSpaceId);
        Optional<FlinkSession> firstValue = flinkSessionList.stream().findFirst();
        if(firstValue.isPresent()){
            putMsg(result, Status.FLINK_SESSION_CLUSTER_RELATED_EXISTS,firstValue.get().getFlinkSessionName());
            return Optional.of(result);
        }

        String projectTaskInstanceName = flinkTaskInstanceMapper.queryTaskInstanceNameByK8sNamespaceId(k8sNameSpaceId);
        if(!StringUtils.isEmpty(projectTaskInstanceName)){
            putMsg(result, Status.FLINK_TASK_INSTANCE_RELATION_EXIST, projectTaskInstanceName);
            return Optional.of(result);
        }
        return Optional.empty();
    }

}
