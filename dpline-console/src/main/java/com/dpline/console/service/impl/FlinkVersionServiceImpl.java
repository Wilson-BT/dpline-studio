package com.dpline.console.service.impl;

import com.dpline.common.enums.Status;
import com.dpline.common.params.CommonProperties;
import com.dpline.common.params.JobConfig;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.FlinkUtils;
import com.dpline.common.util.Result;
import com.dpline.common.util.StringUtils;
import com.dpline.console.service.FlinkVersionService;
import com.dpline.console.service.GenericService;
import com.dpline.dao.entity.FlinkVersion;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.mapper.FlinkVersionMapper;
import com.dpline.dao.rto.FlinkVersionRto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlinkVersionServiceImpl extends GenericService<FlinkVersion, Long> implements FlinkVersionService {

    private static final Logger logger = LoggerFactory.getLogger(FlinkVersionServiceImpl.class);

    public FlinkVersionServiceImpl(@Autowired FlinkVersionMapper flinkVersionMapper) {
        super(flinkVersionMapper);
    }

    public FlinkVersionMapper getMapper() {
        return (FlinkVersionMapper) super.genericMapper;
    }


    @Override
    public Result<Object> create(FlinkVersion flinkVersion) {
        Result<Object> result = new Result<>();
        if(StringUtils.isBlank(flinkVersion.getRealVersion())){
            // 从路径中获取 版本信息
            putMsg(result, Status.FLINK_REAL_VERSION_NOT_EXISTS);
            return result;
        }
        flinkVersion.setFlinkPath(CommonProperties.pathDelimiterResolve(flinkVersion.getFlinkPath()));
        Optional<String> realVersionFromFlinkHome = FlinkUtils.getRealVersionFromFlinkHome(flinkVersion.getFlinkPath(), false);
        if(!realVersionFromFlinkHome.isPresent() || !realVersionFromFlinkHome.get().equals(flinkVersion.getRealVersion())){
            putMsg(result, Status.FLINK_REAL_VERSION_NOT_EXISTS);
            return result;
        }
        return result.setData(insert(flinkVersion)).ok();
    }

    @Override
    public int delete(FlinkVersion flinkVersion) {
        int deleteFlag = delete(flinkVersion.getId());
        return deleteFlag;
    }

    @Override
    public int updateState(FlinkVersion flinkVersion) {
        if (flinkVersion == null || flinkVersion.getId() == null || flinkVersion.getEnabledFlag() == null) {
            return -1;
        }
        FlinkVersion updatePo = new FlinkVersion();
        updatePo.setId(flinkVersion.getId());
        updatePo.setEnabledFlag(flinkVersion.getEnabledFlag());
        int update = update(updatePo);
        return update;
    }

    @Override
    public Result<Object> updateInfo(FlinkVersion flinkVersion) {
        Result<Object> result = new Result<>();
        result.ok();
        if (flinkVersion == null) {
            putMsg(result,Status.FLINK_VERSION_NOT_EXISTS);
            return result.setData(-1);
        }
        if(StringUtils.isEmpty(flinkVersion.getFlinkPath())){
            putMsg(result, Status.FLINK_PATH_NOT_EXISTS);
            return result;
        }
        flinkVersion.setFlinkPath(CommonProperties.pathDelimiterResolve(flinkVersion.getFlinkPath()));
        return result.setData(update(flinkVersion)).ok();
    }

    @Override
    public Pagination<FlinkVersion> list(FlinkVersionRto flinkVersionRto) {
        Pagination<FlinkVersion> flinkVersionPagination = Pagination.getInstanceFromRto(flinkVersionRto);
        this.executePagination(x -> this.getMapper().list(x), flinkVersionPagination);
        return flinkVersionPagination;
    }

    @Override
    public Result<Object> queryFlinkVersion() {
        Result<Object> result = new Result<>();
        result.setData(this.getMapper().queryFlinkVersion());
        result.ok();
        return result;
    }

    @Override
    public Result<Object> searchFlinkVersion(String motorType) {
        Result<Object> result = new Result<>();
        List<FlinkVersion> flinkVersions = this.getMapper().selectByMotorType(motorType);
        return result.setData(flinkVersions).ok();
    }

    @Override
    public Result<Object> selectFlinkVersionById(Long flinkVersionId){
        Result<Object> result = new Result<>();
        if(Asserts.isZero(flinkVersionId)){
            putMsg(result,Status.FLINK_VERSION_NOT_EXISTS);
            return result;
        }
        FlinkVersion flinkVersion = this.getMapper().selectById(flinkVersionId);
        if (Asserts.isNull(flinkVersion)){
            putMsg(result,Status.FLINK_VERSION_NOT_EXISTS);
            return result;
        }
        JobConfig.MotorVersion motorVersion = new JobConfig.MotorVersion();
        motorVersion.setMotorId(flinkVersion.getId());
        motorVersion.setMotorPath(flinkVersion.getFlinkPath());
        motorVersion.setMotorRealVersion(flinkVersion.getRealVersion());
        return result.setData(motorVersion).ok();
    }
}
