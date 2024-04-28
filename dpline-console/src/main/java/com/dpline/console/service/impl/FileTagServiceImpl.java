package com.dpline.console.service.impl;

import com.dpline.common.enums.*;
import com.dpline.common.util.*;
import com.dpline.console.service.GenericService;
import com.dpline.common.util.JobConfigSerialization;
import com.dpline.common.params.DataStreamConfig;
import com.dpline.common.params.JobConfig;
import com.dpline.dao.dto.FileTagDto;
import com.dpline.dao.entity.*;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.mapper.FileTagMapper;
import com.dpline.dao.rto.FileTagRto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class FileTagServiceImpl extends GenericService<FileTag, Long> {

    @Autowired
    FileServiceImpl fileServiceImpl;

    @Autowired
    JarFileServiceImpl jarFileServiceImpl;

    @Autowired
    JobServiceImpl jobServiceImpl;

    @Autowired
    MainResourceFileServiceImpl mainResourceFileService;


    public FileTagServiceImpl(FileTagMapper fileTagMapper) {
        super(fileTagMapper);
    }

    public FileTagMapper getMapper() {
        return (FileTagMapper) this.genericMapper;
    }

    public List<FileTag> getTagByFileId(Pagination<FileTag> fileId) {
        return getMapper().getTagsByFileId(fileId);
    }

    public boolean existByName(Long fileId, String fileTagName) {
        return Asserts.isNotNull(getMapper().getTagsByFileIdAndName(fileId, fileTagName));
    }

    public Result<Object> getTags(FileTagRto fileTagRto) {
        Result<Object> result = new Result<>();
//        Long fileId = fileTagDto.getFileId();
        Pagination<FileTag> clusterPagination = Pagination.getInstanceFromRto(fileTagRto);
        executePagination(this::getTagByFileId, clusterPagination);
        result.ok();
        result.setData(clusterPagination);
        return result;
    }

    /**
     * 删除tag
     *
     * @param fileTag
     * @return
     */
    @Transactional
    public Result<Object> deleteTag(FileTag fileTag) {
        Result<Object> result = new Result<>();
        Long id = fileTag.getId();
        result.ok();
        result.setData(this.getMapper().deleteById(id));
        return result;
    }

    @Transactional
    public Result<Object> addTag(FileTagRto fileTagDto) {
        Result<Object> result = new Result<>();
        Long fileId = fileTagDto.getFileId();
        String fileTagName = fileTagDto.getFileTagName();
        String remark = fileTagDto.getRemark();
        if (StringUtils.isEmpty(fileTagName)) {
            putMsg(result, Status.NAME_NOT_EXIT_ERROR);
            return result;
        }
        if (this.existByName(fileId, fileTagName)) {
            putMsg(result, Status.NAME_EXIST, fileTagName);
            return result;
        }
        File file = fileServiceImpl.getMapper().selectById(fileId);
        FileTag fileTag = new FileTag();
        BeanUtils.copyProperties(file, fileTag);
        try {
            fileTag.setId(CodeGenerateUtils.getInstance().genCode());
        } catch (CodeGenerateUtils.CodeGenerateException e) {
            e.printStackTrace();
        }
        fileTag.setRemark(remark);
        fileTag.setFileId(fileId);
        fileTag.setFileContent(file.getContent());
        fileTag.setFileTagName(fileTagName);
        result.setData(insert(fileTag));
        result.ok();
        return result;
    }


    /**
     * 对比历史版本数据和当前版本数据
     *
     * @param fileTagRto
     * @return
     */
    public Result<Object> compareCurrentAndTagFile(FileTagRto fileTagRto) {
        Result<Object> result = new Result<>();
        result.ok();
        ArrayList<FileTagDto> fileTagDtoList = new ArrayList<>();
        String compareSource = fileTagRto.getCompareSource();
        switch (CompareSource.of(compareSource)) {
            case FILE_COMPARE:
                try {
                    compareFile(fileTagRto, fileTagDtoList);
                    result.setData(fileTagDtoList);
                } catch (RuntimeException e) {
                    logger.error(e.toString());
                    // 资源不存在
                    putMsg(result,Status.JAR_FILE_NOT_EXIST);
                }
                break;
            case JOB_COMPARE:
                // TODO 和线上任务作对比
                //  compareJob(versionBO, result);
                break;
            default:
                putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, compareSource);
        }
        return result;
    }

    private void compareFile(FileTagRto fileTagRto, List<FileTagDto> fileTagDtoList) {
        // 历史版本
        FileTag currentFileTag = this.getMapper().selectById(fileTagRto.getFileTagId());
        FileTagDto currentFileTagDto = new FileTagDto();
        flatMapTagDetail(currentFileTag, currentFileTagDto);

        // 正在编辑的文件
        FileTagDto fileTagDraftDto = new FileTagDto();
        File fileDraft = fileServiceImpl.getMapper().selectById(fileTagRto.getFileId());
        flatMapTagDetail(fileDraft, fileTagDraftDto);
        fileTagDtoList.add(fileTagDraftDto);
        fileTagDtoList.add(currentFileTagDto);
    }

    private FileTagDto flatMapTagDetail(File fileDraft, FileTagDto fileTagDto) {
        // 重新设置 配置 结构，将 yaml 解析为文件
        if (Objects.nonNull(fileDraft.getConfigContent())) {
            String configContent = fileDraft.getConfigContent();
            fileDraft.setConfigContent(handleYaml(configContent));
        }
        if (Objects.nonNull(fileDraft.getSourceContent())) {
            String sourceContent = fileDraft.getSourceContent();
            fileDraft.setSourceContent(JSONUtils.formatToBeautifulString(sourceContent));
        }
        if (Objects.nonNull(fileDraft.getDataStreamContent())) {
            String dataStreamConfig = fileDraft.getDataStreamContent();
            DataStreamConfig streamConfig = JSONUtils.parseObject(dataStreamConfig, DataStreamConfig.class);
            MainResourceFile mainResourceFile = mainResourceFileService.getMapper().selectById(streamConfig.getMainResourceId());
            if (Asserts.isNull(mainResourceFile)) {
                throw new RuntimeException("Main Jar资源不存在");
            }
            streamConfig.setMainResourceId(mainResourceFile.getId());
            String streamStr = JSONUtils.toBeautifulString(streamConfig);
            fileDraft.setDataStreamContent(streamStr);

        }
        BeanUtils.copyProperties(fileDraft, fileTagDto);
        fileTagDto.setFileName(fileDraft.getFileName());
        fileTagDto.setConfigContent(fileDraft.getConfigContent());
        fileTagDto.setFileContent(DeflaterUtils.unzipString(fileDraft.getContent()));
        fileTagDto.setSourceContent(fileDraft.getSourceContent());
        return fileTagDto;
    }

    private FileTagDto flatMapTagDetail(FileTag fileTag, FileTagDto fileTagDto) {
        if (StringUtils.isNotBlank(fileTag.getConfigContent())) {
            String configContent = fileTag.getConfigContent();
            fileTag.setConfigContent(handleYaml(configContent));
        }
        if (StringUtils.isNotBlank(fileTag.getSourceContent())) {
            String sourceContent = fileTag.getSourceContent();
            fileTag.setSourceContent(JSONUtils.formatToBeautifulString(sourceContent));
        }
        if (StringUtils.isNotBlank(fileTag.getDataStreamContent())) {
            String dataStreamConfig = fileTag.getDataStreamContent();
            DataStreamConfig streamConfig = JSONUtils.parseObject(dataStreamConfig, DataStreamConfig.class);
            MainResourceFile mainResourceFile = mainResourceFileService.getMapper().selectById(streamConfig.getMainResourceId());
            streamConfig.setMainResourceId(mainResourceFile.getId());
            dataStreamConfig = JSONUtils.toBeautifulString(streamConfig);
            fileTag.setDataStreamContent(dataStreamConfig);
        }
        BeanUtils.copyProperties(fileTag, fileTagDto);
        fileTagDto.setFileName(fileTag.getFileName());
        fileTagDto.setConfigContent(fileTag.getConfigContent());
        fileTagDto.setFileContent(DeflaterUtils.unzipString(fileTag.getFileContent()));
        fileTagDto.setSourceContent(fileTag.getSourceContent());
        return fileTagDto;
    }

    /**
     * 将 configContent 转为map
     *
     * @param configContent
     * @return
     */
    private String handleYaml(String configContent) {
        JobConfig jobConfig = JSONUtils.parseObject(configContent, JobConfig.class);
        if (Asserts.isNull(jobConfig)){
            return "";
        }
        Map<String, Object> stringObjectMap = JobConfigSerialization.parseYamlToMap(
            jobConfig.getFlinkYaml());
        jobConfig.getRuntimeOptions().setOtherParams(stringObjectMap);
        jobConfig.setFlinkYaml(null);
        configContent = JSONUtils.toBeautifulString(jobConfig);
        return configContent;
    }


    @Transactional
    public Result<Object> rollback(FileTagRto fileTagRto) {
        Result<Object> result = new Result<>();
        FileTag fileTag = this.getMapper().selectById(fileTagRto.getFileTagId());
        File file = fileServiceImpl.getMapper().selectById(fileTag.getFileId());
        file.setContent(fileTag.getFileContent());
        file.setMetaTableContent(fileTag.getMetaTableContent());
        file.setEtlContent(fileTag.getEtlContent());
        file.setConfigContent(fileTag.getConfigContent());
        file.setDataStreamContent(fileTag.getDataStreamContent());
        file.setSourceContent(fileTag.getSourceContent());
        return result.setData(fileServiceImpl.updateSelective(file)).ok();
    }

    /**
     * 上线
     *
     * @param fileTagRto
     * @return
     */
    public Result<Object> online(FileTagRto fileTagRto,Long projectId) {
        Result<Object> result = new Result<>();
        result.ok();

        FileTag fileTag = this.getMapper().selectById(fileTagRto.getFileTagId());
        JobConfig jobConfig = JSONUtils.parseObject(fileTag.getConfigContent(), JobConfig.class);
        if(Asserts.isNull(jobConfig)){
            putMsg(result,Status.JOB_CONFIG_NOT_EXIST);
            return result;
        }
        Long clusterId = Optional.ofNullable(jobConfig.getRunClusterInfo()).map(JobConfig.RunClusterInfo::getClusterId).orElse(null);
        if (Asserts.isNull(clusterId)) {
            putMsg(result, Status.JOB_CLUSTER_NOT_EXIST);
            return result;
        }

        List<Job> jobList = jobServiceImpl.getMapper().selectByNameAndClusterId(fileTagRto.getJobName(),
                                                                    clusterId,
                                                                    projectId);
        if(CollectionUtils.isNotEmpty(jobList)){
            putMsg(result,Status.SAME_JOB_NAME_EXIST);
            return result;
        }
        if(!result.getCode().equals(Status.SUCCESS.getCode())){
            return result;
        }

        // 部署jar包，部署文件，形成job记录
        Job job = new Job();
        job.setFileId(fileTag.getFileId());
        job.setJobName(fileTagRto.getJobName());
        job.setJobContent(fileTag.getFileContent());
        job.setSourceContent(fileTag.getSourceContent());
        job.setProjectId(fileTag.getProjectId());
        job.setFileTagId(fileTag.getId());
        job.setDeployed(Flag.NO.getCode());
        try {
            long jobId = CodeGenerateUtils.getInstance().genCode();
            job.setId(jobId);
            job.setRunJobId(new JobId().toHexString());
        } catch (CodeGenerateUtils.CodeGenerateException e) {
            e.printStackTrace();
        }
        job.setRunMotorType(fileTag.getRunMotorType());
        job.setExecStatus(ExecStatus.NONE.name());
        job.setFileType(fileTag.getFileType());
        RunModeType runModeType = RunModeType.parseRunModeType(
            ClusterType.of(jobConfig.getRunClusterInfo().getClusterType()),
            RunMode.of(jobConfig.getRunMode())
        );
        job.setRunModeType(runModeType.name());
        // 任务集群
        job.setClusterId(clusterId);
        if(Asserts.isNotNull(jobConfig.getRunImageInfo())){
            job.setImageId(jobConfig.getRunImageInfo().getImageId());
        }
        job.setRuntimeOptions(JSONUtils.toJsonString(jobConfig.getRuntimeOptions()));
        // 任务引擎版本
        job.setMotorVersionId(jobConfig.getMotorVersion().getMotorId());
        DataStreamConfig dataStreamConfig = JSONUtils.parseObject(fileTag.getDataStreamContent(), DataStreamConfig.class);
        if(Asserts.isNull(dataStreamConfig)){
            putMsg(result,Status.DATA_STREAM_CONFIG_NULL_ERROR);
            return result;
        }
        MainResourceFile mainResourceFile = mainResourceFileService.getMapper().selectById(dataStreamConfig.getMainResourceId());
        // 主文件不存在
        if(Asserts.isNull(mainResourceFile)){
            putMsg(result,Status.JAR_FILE_NOT_EXIST);
            return result;
        }
        job.setMainResourceId(dataStreamConfig.getMainResourceId());
        job.setMainClass(dataStreamConfig.getMainClass());
        job.setAppArgs(dataStreamConfig.getAppArgs());
        job.setOtherRuntimeConfig(jobConfig.getFlinkYaml());
        jobServiceImpl.insert(job);
        return result.ok();
    }
}
