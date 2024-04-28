package com.dpline.console.service.impl;

import com.dpline.common.enums.Status;
import com.dpline.common.params.JobConfig;
import com.dpline.common.util.DockerImageDetailParser;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.CollectionUtils;
import com.dpline.common.util.DockerUtil;
import com.dpline.common.util.Result;
import com.dpline.console.exception.ServiceException;
import com.dpline.console.service.GenericService;
import com.dpline.dao.entity.DockerImage;
import com.dpline.dao.entity.Job;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.mapper.DockerImageMapper;
import com.dpline.dao.rto.DockerImageRto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;


@Service
public class DockerImageServiceImpl extends GenericService<DockerImage, Long> {

    public DockerImageMapper getMapper() {
        return (DockerImageMapper)this.genericMapper;
    }

    @Autowired
    private DockerImageMapper dockerImageMapper;


    @Autowired
    private JobServiceImpl jobServiceImpl;

    public DockerImageServiceImpl(@Autowired DockerImageMapper genericMapper) {
        super(genericMapper);
    }


    public DockerImageMapper getDockerImageMapper() {
        return (DockerImageMapper) genericMapper;
    }

    @Transactional
    public Result<Object> createDockerImageInstance(DockerImageRto dockerImageRto) {
        Result<Object> result = new Result<>();
        putMsg(result, Status.SUCCESS);
        if (existSameImageName(dockerImageRto.getShortName())) {
            putMsg(result, Status.DOCKER_IMAGE_NAME_EXIST_ERROR);
            return result;
        }
        try {
            DockerUtil dockerUtil = new DockerUtil();
            if (dockerUtil.checkDockerHarborImage(dockerImageRto.getRegisterUser(), dockerImageRto.getRegisterPassword(), dockerImageRto.getRegisterAddress(), dockerImageRto.getImageName()).get()) {
                DockerImageDetailParser dockerImageParser = dockerUtil.getDockerImage();
                DockerImage dockerImage = new DockerImage();
                dockerImage.setImageName(dockerImageParser.getWholeImage());
                dockerImage.setShortName(dockerImageRto.getShortName());
                dockerImage.setRegisterAddress(dockerImageRto.getRegisterAddress());
                dockerImage.setRegisterPassword(dockerImageRto.getRegisterPassword());
                dockerImage.setRegisterUser(dockerImageRto.getRegisterUser());
                dockerImage.setMotorType(dockerImageRto.getMotorType());
                dockerImage.setMotorVersionId(dockerImageRto.getMotorVersionId());
                this.insertSelective(dockerImage);
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
    public Result<Object> queryAllImages(DockerImageRto dockerImageRto) {
        Result<Object> result = new Result<>();
        Pagination<DockerImage> instanceFromRto = Pagination.getInstanceFromRto(dockerImageRto);
        executePagination(x->this.getDockerImageMapper().queryAllImages(x),instanceFromRto);
        result.setData(instanceFromRto).ok();
        return result;
    }

    public Result<Object> updateDockerImage(DockerImageRto dockerImageRto) {
        Result<Object> result = new Result<>();
        DockerImage dockerImage = dockerImageMapper.selectById(dockerImageRto.getId());
        if (!dockerImageRto.getShortName().equals(dockerImage.getShortName()) && existSameImageName(dockerImageRto.getShortName())) {
            putMsg(result, Status.DOCKER_IMAGE_NAME_EXIST_ERROR);
            return result;
        }
        try {
            DockerUtil dockerUtil = new DockerUtil();
            if (dockerUtil.checkDockerHarborImage(dockerImageRto.getRegisterUser(),
                dockerImageRto.getRegisterPassword(),
                dockerImageRto.getRegisterAddress(),
                dockerImageRto.getImageName()).get()) {
                dockerImage.setImageName(dockerUtil.getDockerImage().getWholeImage());
                dockerImage.setShortName(dockerImageRto.getShortName());
                dockerImage.setRegisterAddress(dockerImageRto.getRegisterAddress());
                dockerImage.setRegisterUser(dockerImageRto.getRegisterUser());
                dockerImage.setRegisterPassword(dockerImageRto.getRegisterPassword());
                dockerImage.setMotorType(dockerImageRto.getMotorType());
                dockerImage.setMotorVersionId(dockerImageRto.getMotorVersionId());
                return result.setData(this.update(dockerImage)).ok();
            }
        } catch (IOException e) {
            logger.error("Docker update failed");
            e.printStackTrace();
        }
        putMsg(result, Status.DOCKER_IMAGE_NOT_EXIST_ERROR);
        return result;
    }

    public Result<Object> deleteDockerImage(DockerImageRto dockerImageRto) {
        Result<Object> result = new Result<>();
        DockerImage dockerImage = dockerImageMapper.selectById(dockerImageRto.getId());
        List<Job> jobList = jobServiceImpl.selectByDockerImageId(dockerImage.getId());
        if (CollectionUtils.isNotEmpty(jobList)) {
            putMsg(result, Status.DOCKER_IMAGE_TASK_BOUND_ERROR, jobList.get(0).getJobName());
            return result;
        }
//        QueryWrapper<FlinkSession> flinkSessionQueryWrapper = new QueryWrapper<>();
//        flinkSessionQueryWrapper.eq("k8s_cluster_id", id).eq("status", Flag.YES.getCode());
//        List<FlinkSession> flinkSessions = flinkSessionMapper.selectList(flinkSessionQueryWrapper);
//        if (CollectionUtils.isNotEmpty(flinkSessions)) {
//            putMsg(result, Status.DOCKER_IMAGE_FLINKSESSION_BOUND_ERROR, flinkSessions.get(0).getFlinkSessionName());
//            return result;
//        }
        return result.setData(dockerImageMapper.deleteById(dockerImageRto.getId())).ok();
    }

    private Boolean existSameImageName(String shortName) {
        return dockerImageMapper.existSameImageName(shortName) == Boolean.TRUE;
    }

    public Result<Object> listDockerImageByVersion(DockerImageRto dockerImageRto) {
        Result<Object> result = new Result<>();
        List<DockerImage> dockerImageList = this.getMapper().listDockerImage(dockerImageRto.getMotorVersionId(),"FLINK");
        return result.setData(dockerImageList).ok();
    }

    public Result<Object> selectImageById(Long imageId) {
        Result<Object> result = new Result<>();
        // 使用 clusterId 查找
        if(Asserts.isZero(imageId)){
            putMsg(result,Status.DOCKER_IMAGE_NOT_EXIST_ERROR);
            return result;
        }

        DockerImage dockerImage = this.getMapper().selectById(imageId);
        // 如果是空，那么直接返回故障
        if(Asserts.isNull(dockerImage)){
            putMsg(result,Status.DOCKER_IMAGE_NOT_EXIST_ERROR);
            return result;
        }
        JobConfig.RunImageInfo runImageInfo = new JobConfig.RunImageInfo();
        runImageInfo.setImageId(imageId);
        runImageInfo.setShortName(dockerImage.getShortName());
        runImageInfo.setImageName(dockerImage.getImageName());
        return result.setData(runImageInfo).ok();
    }
}
