package com.handsome.console.service.impl;

import com.handsome.common.enums.ReleaseState;
import com.handsome.common.enums.ResFsType;
import com.handsome.common.enums.Status;
import com.handsome.common.util.FileUtils;
import com.handsome.common.util.FlinkUtils;
import com.handsome.common.util.StringUtils;
import com.handsome.dao.entity.FlinkVersion;
import com.handsome.dao.entity.User;
import com.handsome.dao.mapper.FlinkTaskDefinitionMapper;
import com.handsome.dao.mapper.FlinkTaskTagLogMapper;
import com.handsome.dao.mapper.FlinkVersionMapper;
import com.handsome.console.exception.ServiceException;
import com.handsome.console.service.FlinkVersionService;
import com.handsome.common.util.Result;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
public class FlinkVersionServiceImpl extends BaseServiceImpl implements FlinkVersionService {

    private static final Logger logger = LoggerFactory.getLogger(FlinkVersionServiceImpl.class);

    @Autowired
    FlinkVersionMapper flinkVersionMapper;

    @Autowired
    FlinkTaskTagLogMapper flinkTaskTagLogMapper;

    @Autowired
    FlinkTaskDefinitionMapper flinkTaskDefinitionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Object> createFlinkVersion(User loginUser, String name, ReleaseState releaseState, String description, String flinkPath) {
        Result<Object> result = new Result<>();
        putMsg(result, Status.SUCCESS);
        if (!isAdmin(loginUser)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }
        // TODO 是否需要检查Flink下面的文件是否满足需求
        try {
            // 文件目录是否存在
            if (!FileUtils.checkDirExist(flinkPath, ResFsType.LOCAL)) {
                putMsg(result, Status.FLINK_CLIENT_NOT_EXISTS,flinkPath);
                return result;
            }
            // Name 是否已经录入
            if (existFlinkVersion(name, null)) {
                putMsg(result, Status.FLINK_VERSION_NAME_EXISTS,name);
                return result;
            }
            FlinkVersion flinkVersionInstance = new FlinkVersion();
            flinkVersionInstance.setFlinkName(name);
            flinkVersionInstance.setFlinkPath(flinkPath);
            flinkVersionInstance.setReleaseState(ReleaseState.OFFLINE);
            flinkVersionInstance.setCreateTime(new Date());
            flinkVersionInstance.setUpdateTime(new Date());
            flinkVersionInstance.setRealVersion(FlinkUtils.getRealVersionFromFlinkHome(flinkPath, false).orElse(null));
            flinkVersionMapper.insert(flinkVersionInstance);
            result.setData(flinkVersionInstance);
        } catch (Exception e) {
            logger.error("Flink Version insert [{}] failed,{}", flinkPath, e.getMessage());
            putMsg(result, Status.CREATE_FLINK_VERSION_ERROR);
            return result;
        }
        return result;
    }

    /**
     * 更新字段,releaseState
     *
     * @param loginUser
     * @param flinkVersionId
     * @param releaseState   发布状态，可以更改
     * @param name           别名，可以更改
     * @param description    描述信息，可以更改
     * @param flinkPath      Flink 路径可以更改
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Object> updateFlinkVersion(User loginUser, int flinkVersionId, ReleaseState releaseState, String name, String description, String flinkPath) {

        Result<Object> result = new Result<>();
        putMsg(result, Status.SUCCESS);
        if (!isAdmin(loginUser)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }

        FlinkVersion oldFlinkVersion = flinkVersionMapper.selectById(flinkVersionId);
        if (null == oldFlinkVersion) {
            putMsg(result, Status.FLINK_CLIENT_NOT_EXISTS);
            return result;
        }
        String realVersion = oldFlinkVersion.getRealVersion();
        try {
            // version has relation task
            if (oldFlinkVersion.getReleaseState().equals(ReleaseState.ONLINE) && releaseState.equals(ReleaseState.OFFLINE)) {
                Optional<Result<Object>> objectResult = existVersionRelation(result, oldFlinkVersion.getId());
                if(objectResult.isPresent()){
                    return result;
                }
            }
            // 名称发生改变,相同名称退出
            boolean equalName = oldFlinkVersion.getFlinkName().equals(name);
            boolean equalFlinkPath = oldFlinkVersion.getFlinkPath().equals(flinkPath);
            if (!equalName && existFlinkVersion(name, null)) {
                putMsg(result, Status.FLINK_VERSION_NAME_EXISTS);
                return result;
            }
            // 路径发生改变，相同路径退出
            if (!equalFlinkPath) {
                if (existFlinkVersion(null, flinkPath)) {
                    putMsg(result, Status.FLINK_VERSION_NAME_EXISTS);
                    return result;
                }
                Optional<String> realVersionFromFlinkHome = FlinkUtils.getRealVersionFromFlinkHome(flinkPath, false);
                if (!realVersionFromFlinkHome.isPresent()) {
                    logger.error(Status.FLINK_CLIENT_NOT_EXISTS.getMsg());
                    putMsg(result, Status.FLINK_CLIENT_NOT_EXISTS);
                    return result;
                }
                realVersion = realVersionFromFlinkHome.get();
            }
            // 如果任何一个发生了改变，但是已经有新的了
            FlinkVersion newFlinkVersionInstance = new FlinkVersion();
            newFlinkVersionInstance.setId(flinkVersionId);
            newFlinkVersionInstance.setFlinkName(name);
            newFlinkVersionInstance.setFlinkPath(flinkPath);
            newFlinkVersionInstance.setReleaseState(releaseState);
            newFlinkVersionInstance.setUpdateTime(new Date());
            newFlinkVersionInstance.setRealVersion(realVersion);
            newFlinkVersionInstance.setDescription(description);
            flinkVersionMapper.updateById(newFlinkVersionInstance);
            return result;
        } catch (ServiceException e) {
            logger.error(e.getMessage());
            putMsg(result, Status.UPDATE_FLINK_VERSION_ERROR);
            return result;
        }
    }

    /**
     * delete  现有的 flinkVersion
     *
     * @param loginUser
     * @param flinkVersionId
     * @return
     */
    @Override
    public Result<Object> deleteFlinkVersion(User loginUser, int flinkVersionId) {
        Result<Object> result = new Result<>();
        putMsg(result, Status.SUCCESS);
        if (!isAdmin(loginUser)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }
        FlinkVersion flinkVersion = flinkVersionMapper.selectById(flinkVersionId);
        if (flinkVersion == null) {
            putMsg(result, Status.FLINK_CLIENT_NOT_EXISTS);
        }
        Optional<Result<Object>> objectResult = existVersionRelation(result, flinkVersionId);
        if (objectResult.isPresent()){
            return objectResult.get();
        }
        flinkVersionMapper.deleteById(flinkVersionId);
        return result;
    }

    /**
     * flink version 是否存在关联的任务草稿、关联的任务tag
     * @param result
     * @param flinkVersionId
     * @return
     */
    Optional<Result<Object>> existVersionRelation(Result<Object> result,int flinkVersionId){
        String flinkTaskName = flinkTaskDefinitionMapper.queryNameByFlinkVersionId(flinkVersionId);
        if (StringUtils.isNotEmpty(flinkTaskName)) {
            putMsg(result, Status.TASK_DEFINE_RELATION_EXIST_ERROR, flinkTaskName);
            return Optional.of(result);
        }
        String flinkTaskTagLogName = flinkTaskTagLogMapper.queryNameByFlinkVersionId(flinkVersionId);
        if (StringUtils.isNotEmpty(flinkTaskTagLogName)) {
            putMsg(result, Status.TASK_TAG_RELATION_EXIST_ERROR, flinkTaskTagLogName);
            return Optional.of(result);
        }
        return Optional.empty();
    }

    /**
     * 检查是否存在一样的名称
     *
     * @param FlinkName
     * @return
     */
    public boolean existFlinkVersion(String FlinkName, String flinkHomePath) {
        return flinkVersionMapper.existFlinkVersion(FlinkName, flinkHomePath) == Boolean.TRUE;
    }

    public List<FlinkVersion> queryAllFlinkVersion(int releaseState) {
        return flinkVersionMapper.queryAllList(releaseState);
    }

    @Override
    public Result<Object> queryFlinkVersionList(ReleaseState releaseState) {
        Result<Object> result = new Result<>();
        List<FlinkVersion> flinkVersions = queryAllFlinkVersion(releaseState.getCode());
        if (CollectionUtils.isEmpty(flinkVersions)) {
            putMsg(result, Status.LIST_FLINK_VERSION_INSTANCE_ERROR);
            return result;
        }
        result.setData(flinkVersions);
        putMsg(result, Status.SUCCESS);
        return result;
    }

}
