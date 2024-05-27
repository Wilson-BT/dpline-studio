package com.dpline.console.service.impl;

import com.dpline.common.Constants;
import com.dpline.common.enums.Flag;
import com.dpline.common.enums.Status;
import com.dpline.common.params.CommonProperties;
import com.dpline.common.params.JobConfig;
import com.dpline.common.store.FsStore;
import com.dpline.common.util.*;
import com.dpline.console.service.FlinkVersionService;
import com.dpline.console.service.GenericService;
import com.dpline.dao.entity.FlinkVersion;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.mapper.FlinkVersionMapper;
import com.dpline.dao.rto.FlinkVersionRto;
import org.apache.hadoop.fs.FileStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class FlinkVersionServiceImpl extends GenericService<FlinkVersion, Long> implements FlinkVersionService {

    @Autowired
    FsStore fsStore;

    private static final Logger logger = LoggerFactory.getLogger(FlinkVersionServiceImpl.class);

    private final static String FLINK_REMOTE_FS_PATH = "/dpline/upload/flink/%s/%s";

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
            putMsg(result, Status.FLINK_REAL_VERSION_NOT_EXISTS);
            return result;
        }
        flinkVersion.setFlinkPath(CommonProperties.pathDelimiterResolve(flinkVersion.getFlinkPath()));
        Optional<String> realVersionFromFlinkHome;
        if(FsStore.WINDOWS){
            realVersionFromFlinkHome = FlinkUtils.getRealVersionFromFlinkHome(flinkVersion.getFlinkPath(), true);
        } else {
            realVersionFromFlinkHome = FlinkUtils.getRealVersionFromFlinkHome(flinkVersion.getFlinkPath(), false);
        }
        if(!realVersionFromFlinkHome.isPresent() || !realVersionFromFlinkHome.get().equals(flinkVersion.getRealVersion())){
            putMsg(result, Status.FLINK_REAL_VERSION_NOT_EXISTS);
            return result;
        }
        CompletableFuture.runAsync(() -> {
            uploadAllJarsToHdfs(TaskPathResolver.pathDelimiterResolve(flinkVersion.getFlinkPath()),flinkVersion.getRealVersion());
        }).exceptionally(ex -> {
            logger.error("Flink version [{}] upload error.", realVersionFromFlinkHome);
            return null;
        }).thenRun(() -> {
            logger.info("Flink version [{}] upload success.", realVersionFromFlinkHome);
            flinkVersion.setEnabledFlag(Flag.YES.getCode());
            update(flinkVersion);
        });
        flinkVersion.setEnabledFlag(Flag.MID.getCode());
        return result.setData(insert(flinkVersion)).ok();
    }

    /**
     * copy all files to hdfs
     * @param flinkPath
     */
    private void uploadAllJarsToHdfs(String flinkPath,String flinkRealVersion) {
        copyAllFilesToHdfs(FileUtils.concatPath(flinkPath,"lib"),String.format(FLINK_REMOTE_FS_PATH,flinkRealVersion,"lib"));
        copyAllFilesToHdfs(FileUtils.concatPath(flinkPath ,"plugins"),String.format(FLINK_REMOTE_FS_PATH,flinkRealVersion,"plugins"));
        copyAllFilesToHdfs(FileUtils.concatPath(flinkPath ,"opt"),String.format(FLINK_REMOTE_FS_PATH,flinkRealVersion,"opt"));
    }

    private void copyAllFilesToHdfs(String localPath, String remotePath) {
        try {
            if(fsStore.exists(remotePath)){
                fsStore.delete(remotePath,true);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File[] files = FileUtils.listFiles(localPath);
        Arrays.stream(files).forEach(file -> {
            try {
                // 如果是目录，
                if(file.isDirectory()){
                    copyAllFilesToHdfs(file.getAbsolutePath(), remotePath + Constants.DIVISION_STRING + file.getName());
                } else {
                    fsStore.upload(file.getAbsolutePath(),
                            TaskPathResolver.pathDelimiterResolve(remotePath) + Constants.DIVISION_STRING + file.getName(),
                            false,true);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Override
    public int delete(FlinkVersion flinkVersion) {
        int deleteFlag = delete(flinkVersion.getId());
        try {
            fsStore.delete(String.format(FLINK_REMOTE_FS_PATH,flinkVersion.getRealVersion(),""),true);
        } catch (IOException e) {
            logger.error("Delete remote Flink path [{}] error.",String.format(FLINK_REMOTE_FS_PATH,flinkVersion.getRealVersion(),""));
            throw new RuntimeException(e);
        }
        return deleteFlag;
    }

    public String getLibRemotePath(String flinkVersion){
        return String.format(FLINK_REMOTE_FS_PATH,flinkVersion,"lib");
    }

    public String getPluginsRemotePath(String flinkVersion){
        return String.format(FLINK_REMOTE_FS_PATH,flinkVersion,"plugins");
    }

    public String getOptRemotePath(String flinkVersion){
        return String.format(FLINK_REMOTE_FS_PATH,flinkVersion,"opt");
    }

    public String getFlinkDistRemoteFsPath(String flinkVersion){
        try {
            String libPath = String.format(FLINK_REMOTE_FS_PATH, flinkVersion, "lib");
            List<FileStatus> opt = fsStore.listAllFiles(libPath,true);
            Optional<FileStatus> dist = opt.stream().filter(fileStatus -> fileStatus.getPath().getName().contains("dist")).findFirst();
            return dist.get().getPath().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
