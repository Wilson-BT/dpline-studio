package com.handsome.console.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.handsome.common.Constants;
import com.handsome.common.enums.Flag;
import com.handsome.common.enums.Status;
import com.handsome.common.params.DockerImageDetailParser;
import com.handsome.common.util.DockerUtil;
import com.handsome.console.exception.ServiceException;
import com.handsome.console.service.DockerImageService;
import com.handsome.common.util.Result;
import com.handsome.dao.entity.DockerImage;
import com.handsome.dao.entity.FlinkRunTaskInstance;
import com.handsome.dao.entity.FlinkSession;
import com.handsome.dao.entity.User;
import com.handsome.dao.mapper.DockerImageMapper;
import com.handsome.dao.mapper.FlinkSessionMapper;
import com.handsome.dao.mapper.FlinkTaskInstanceMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class DockerImageServiceImpl extends BaseServiceImpl implements DockerImageService {

    @Autowired
    private DockerImageMapper dockerImageMapper;

    @Autowired
    FlinkSessionMapper flinkSessionMapper;

    @Autowired
    FlinkTaskInstanceMapper flinkTaskInstanceMapper;

    @Override
    public Map<String, Object> createDockerImageInstance(User loginUser, String imageName, String aliasName, String registerAddress, String registerPassword, String registerUser) {
        Map<String, Object> result = new HashMap<>();
        putMsg(result, Status.SUCCESS);
        if (isNotAdmin(loginUser, result)) {
            return result;
        }
        if (existSameImageName(imageName)) {
            putMsg(result, Status.DOCKER_IMAGE_NAME_EXIST_ERROR);
            return result;
        }
        try {
            DockerUtil dockerUtil = new DockerUtil();
            if (dockerUtil.checkDockerHarborImage(registerUser, registerPassword, registerAddress, imageName).get()) {
                DockerImageDetailParser dockerImageParser = dockerUtil.getDockerImage();
                Date now = new Date();
                DockerImage dockerImage = new DockerImage();
                dockerImage.setImageName(dockerImageParser.getWholeImage());
                dockerImage.setAliasName(aliasName);
                dockerImage.setRegisterAddress(registerAddress);
                dockerImage.setRegisterPassword(registerPassword);
                dockerImage.setRegisterUser(registerUser);
                dockerImage.setCreateTime(now);
                dockerImage.setUpdateTime(now);
                dockerImageMapper.insert(dockerImage);
                result.put(Constants.DATA_LIST, dockerImage);
                return result;
            }
            putMsg(result, Status.DOCKER_IMAGE_NOT_EXIST_ERROR);
            return result;
        } catch (IOException e) {
            throw new ServiceException(e.toString());
        }
    }

    /**
     * query all docker image
     *
     * @return
     */
    @Override
    public Result<Object> queryAllImages() {
        Result<Object> result = new Result<>();
        List<DockerImage> dockerImageList = dockerImageMapper.queryAllImages();
        result.setData(dockerImageList);
        return result;
    }

    /**
     * update docker image
     *
     * @param loginUser
     * @param imageName
     * @param aliasName
     * @param registerAddress
     * @param registerPassword
     * @param registerUser
     * @return
     */
    @Override
    public Result<Object> updateDockerImage(User loginUser, int id, String imageName,
                                            String aliasName, String registerAddress,
                                            String registerPassword, String registerUser) {
        Result<Object> result = new Result<>();
        if (isNotAdmin(loginUser, result)) {
            return result;
        }
        DockerImage dockerImage = dockerImageMapper.selectById(id);
        if (!imageName.equals(dockerImage.getImageName()) && existSameImageName(imageName)) {
            putMsg(result, Status.DOCKER_IMAGE_NAME_EXIST_ERROR);
            return result;
        }
        try {
            DockerUtil dockerUtil = new DockerUtil();
            if (dockerUtil.checkDockerHarborImage(registerUser, registerPassword, registerAddress, imageName).get()) {
                Date now = new Date();
                dockerImage.setImageName(dockerUtil.getDockerImage().getWholeImage());
                dockerImage.setAliasName(aliasName);
                dockerImage.setRegisterAddress(registerAddress);
                dockerImage.setRegisterPassword(registerPassword);
                dockerImage.setUpdateTime(now);
                dockerImageMapper.updateById(dockerImage);
                result.setData(dockerImage);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        putMsg(result, Status.DOCKER_IMAGE_NOT_EXIST_ERROR);
        return result;
    }

    @Override
    public Result<Object> deleteDockerImage(User loginUser, int id) {
        Result<Object> result = new Result<>();
        if (isNotAdmin(loginUser, result)) {
            return result;
        }
        DockerImage dockerImage = dockerImageMapper.selectById(id);
        FlinkRunTaskInstance flinkRunTaskInstance = flinkTaskInstanceMapper.selectByDockerImageId(dockerImage.getId());
        if (flinkRunTaskInstance != null) {
            putMsg(result, Status.DOCKER_IMAGE_TASK_BOUND_ERROR, flinkRunTaskInstance.getFlinkTaskInstanceName());
            return result;
        }
        QueryWrapper<FlinkSession> flinkSessionQueryWrapper = new QueryWrapper<>();
        flinkSessionQueryWrapper.eq("k8s_cluster_id", id).eq("status", Flag.YES.getCode());
        List<FlinkSession> flinkSessions = flinkSessionMapper.selectList(flinkSessionQueryWrapper);
        if (CollectionUtils.isNotEmpty(flinkSessions)) {
            putMsg(result, Status.DOCKER_IMAGE_FLINKSESSION_BOUND_ERROR, flinkSessions.get(0).getFlinkSessionName());
            return result;
        }
        dockerImageMapper.deleteById(id);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    private Boolean existSameImageName(String imageName) {
        return dockerImageMapper.existSameImageName(imageName) == Boolean.TRUE;
    }

}
