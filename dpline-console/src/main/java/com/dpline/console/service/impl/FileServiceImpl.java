package com.dpline.console.service.impl;

import com.dpline.common.Constants;
import com.dpline.common.enums.*;
import com.dpline.common.params.FlinkHomeOptions;
import com.dpline.common.request.FlinkDagRequest;
import com.dpline.common.request.FlinkDagResponse;
import com.dpline.common.request.JarResource;
import com.dpline.common.util.*;
import com.dpline.console.config.FlinkConfigProperties;
import com.dpline.console.service.GenericService;
import com.dpline.console.service.NettyClientService;
import com.dpline.console.util.Context;
import com.dpline.console.util.ContextUtils;
import com.dpline.common.params.DataStreamConfig;
import com.dpline.common.params.JobConfig;
import com.dpline.dao.domain.SourceConfig;
import com.dpline.dao.dto.DplineFileDto;
import com.dpline.dao.dto.FileDto;
import com.dpline.dao.dto.FolderDto;
import com.dpline.dao.entity.*;
import com.dpline.dao.entity.FlinkVersion;
import com.dpline.dao.mapper.FileMapper;
import com.dpline.dao.rto.FileTagRto;
import com.dpline.remote.command.FileDagCommand;
import com.dpline.remote.command.FileDagResponseCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class FileServiceImpl extends GenericService<File, Long> {

    @Autowired
    UsersServiceImpl usersServiceImpl;

    @Autowired
    ProjectServiceImpl projectServiceImpl;

    @Autowired
    ProjectUserServiceImpl projectUserServiceImpl;

    @Autowired
    JobServiceImpl jobServiceImpl;

    @Autowired
    FileTagServiceImpl fileTagServiceImpl;

    @Autowired
    FolderServiceImpl folderServiceImpl;

    @Autowired
    FlinkVersionServiceImpl flinkVersionServiceImpl;

    @Autowired
    ClusterServiceImpl clusterServiceImpl;

    @Autowired
    JarFileServiceImpl jarFileServiceImpl;

    @Autowired
    NettyClientService nettyClientService;


    @Autowired
    FlinkConfigProperties flinkConfigProperties;


    @Autowired
    MainResourceFileServiceImpl mainResourceFileServiceImpl;

    private static final String NAME_REGULAR = "([A-Za-z0-9_\\-])+";
    private static final String SQL_CONTENT = "%s\r\n%s";

    private static Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public FileServiceImpl(@Autowired FileMapper fileMapper) {
        super(fileMapper);
    }


    public Object fileHistory(User logUser) {
        List<DplineFileDto> fhList = null;
        if (UserType.GENERAL_USER.getCode() == logUser.getIsAdmin()) {
            fhList = this.getMapper().getFileHistory4Common(logUser.getUserCode());
        } else {
            fhList = this.getMapper().getFileHistory(logUser.getUserCode());
        }
        return fhList;
    }

    public FileMapper getMapper() {
        return (FileMapper) this.genericMapper;
    }

    /**
     * 只要用户拥有本项目权限，就有修改任务的权限
     * @param projectId
     * @return
     */
    public boolean allowAddJob(Long projectId) {
        Context context = ContextUtils.get();
        ProjectUser projectUser = projectUserServiceImpl.getMapper().selectProjectUser(projectId, context.getUserCode());
        return Asserts.isNotNull(projectUser);
    }

    public Result<Object> queryFile(FileDto fileDto) {
        // 用户有无这个项目权限
        Result<Object> result = new Result<>();
        if(!projectUserServiceImpl.hasQueryPerm(ContextUtils.get().getUser(), fileDto.getProjectId())){
            putMsg(result,Status.USER_NO_OPERATION_PERM);
            return result;
        }
        Map<Long, FileDto> fileDtoListMap = getMapper().selectFiles(fileDto);
        List<Long> fileIdList = fileDtoListMap.values().stream().map(FileDto::getId).collect(Collectors.toList());
        Map<Long, byte[]> onlineFileMap = jobServiceImpl.isOnlineFile(fileIdList);

        ArrayList<FileDto> fileListResp = new ArrayList<>();
        fileDtoListMap.forEach((key,value) -> {
            FileDto fileDtoResp = new FileDto();
            BeanUtils.copyProperties(value, fileDtoResp);
            if (onlineFileMap.containsKey(key)){
                fileDtoResp.setOnLine(Flag.YES.getCode());
            } else {
                fileDtoResp.setOnLine(Flag.NO.getCode());
            }
            fileDtoResp.setJobConfig(JSONUtils.parseObject(value.getConfigContent(), JobConfig.class));
            fileDtoResp.setSourceConfig(JSONUtils.parseObject(value.getSourceContent(), SourceConfig.class));
            fileDtoResp.setConfigContent(null);
            fileDtoResp.setSourceContent(null);
            fileListResp.add(fileDtoResp);
        });
        return result.setData(fileListResp).ok();
    }

    @Transactional
    public Result<Object> addFile(FileDto fileDto) {
        Result<Object> result = new Result<>();
        File file = new File();
        BeanUtils.copyProperties(fileDto,file);
        // 文件名要符合规范
        if(!fileDto.getFileName().matches(NAME_REGULAR)){
            putMsg(result,Status.FILE_NAME_NOT_VALID);
            return result;
        }
        File fileByName = getMapper().getFileByName(file);
        if (Asserts.isNotNull(fileByName)){
            putMsg(result,Status.FILE_NAME_EXIST_ERROR);
            return result;
        }
        // 文件名在测试环境或者生产环境下已经存在
        if(fileDto.getFileType().equals(FileType.SQL_STREAM.getType())){
            fileDto.getJobConfig().setFlinkYaml(DeflaterUtils.zipString(flinkConfigProperties.getSqlTemplate()));
        }else {
            fileDto.getJobConfig().setFlinkYaml(DeflaterUtils.zipString(flinkConfigProperties.getDStreamTemplate()));
        }
        // 使用 默认的 flink 版本
        file.setConfigContent(JSONUtils.toJsonString(fileDto.getJobConfig()));
        file.setSourceContent(JSONUtils.toJsonString(fileDto.getSourceConfig()));
        mergeFileMetaAndSqlContent(fileDto,file);
        file.setFileStatus(FileStatus.CREATED.name());
        if(StringUtils.isNotBlank(file.getContent())){
            file.setContent(DeflaterUtils.zipString(file.getContent()));
        }
        if(StringUtils.isNotBlank(file.getEtlContent())){
            file.setEtlContent(DeflaterUtils.zipString(file.getEtlContent()));
        }
        if(StringUtils.isNotBlank(file.getMetaTableContent())){
            file.setMetaTableContent(DeflaterUtils.zipString(file.getMetaTableContent()));
        }
        file.setRunMotorType(RunMotorType.FLINK.name());

        // 如果是 sql 模式，需要自动填充 dataStreamConfig 为 dpline-app.jar
        if (fileDto.getFileType().equals(FileType.SQL_STREAM.getType())){
            DataStreamConfig dataStreamConfig = new DataStreamConfig();
            // MAIN 需要有 版本,
            List<MainResourceFile> mainResourceFiles = mainResourceFileServiceImpl.getMapper().searchSourceByName(
                Constants.SQL_MAIN_JAR,
                JarType.AuthType.PUBLIC.getValue(),
                JarType.FunctionType.MAIN.getValue(),
                file.getProjectId()
            );
            if(CollectionUtils.isEmpty(mainResourceFiles) || mainResourceFiles.size() > 1){
                putMsg(result,Status.MAIN_FILE_NOT_EXIST_ERROR);
                return result;
            }
            MainResourceFile mainResourceFile = mainResourceFiles.get(0);
            dataStreamConfig.setMainResourceId(mainResourceFile.getId());
            dataStreamConfig.setName(mainResourceFile.getName());
            dataStreamConfig.setMainClass(Constants.SQL_MAIN_CLASS);
            file.setDataStreamContent(JSONUtils.toJsonString(dataStreamConfig));
        } else {
            if (Asserts.isNotNull(fileDto.getDataStreamConfig())) {
                file.setDataStreamContent(JSONUtils.toJsonString(fileDto.getDataStreamConfig()));
            }
        }
        result.setData(insert(file));
        result.ok();
        return result;
    }


    /**
     * 整合
     * @param fileDto
     * @param file
     */
    private void mergeFileMetaAndSqlContent(FileDto fileDto, File file) {
        if(!FileType.SQL_STREAM.getType().equals(fileDto.getFileType())){
            return;
        }
        String newEtlContent = Constants.SPACE;
        String newMetaContent = Constants.SPACE;
        //移除空行
        if (!StringUtils.isEmpty(fileDto.getMetaTableContent())) {
            newMetaContent = fileDto.getMetaTableContent();
        }
        if (!StringUtils.isEmpty(fileDto.getEtlContent())) {
            newEtlContent = fileDto.getEtlContent();
        }
        file.setContent(String.format(SQL_CONTENT, newMetaContent, newEtlContent));
    }


    public Result<Object> getDetailFile(FileDto fileDto) {
        Result<Object> result = new Result<>();
        User user = ContextUtils.get().getUser();
        fileDto.setUserCode(user.getUserCode());
        FileDto detailFile = this.getMapper().getDetailFile(fileDto);
        List<Job> jobByFileId = jobServiceImpl.getJobByFileId(fileDto);
        if(CollectionUtils.isNotEmpty(jobByFileId)){
            detailFile.setOnLine(Flag.YES.getCode());
        }
        if (Objects.isNull(detailFile)) {
            putMsg(result, Status.FILE_NOT_EXIST_EXCEPTION);
            return result;
        }
        JobConfig jobConfig = JSONUtils.parseObject(detailFile.getConfigContent(), JobConfig.class);
        if(Asserts.isNull(jobConfig)){
            putMsg(result,Status.FILE_YML_CONFIG_NOT_EXIT);
            return result;
        }
        jobConfig.setFlinkYaml(DeflaterUtils.unzipString(jobConfig.getFlinkYaml()));
        detailFile.setConfigContent(JSONUtils.toJsonString(jobConfig));
        if(StringUtils.isNotBlank(detailFile.getEtlContent())){
            detailFile.setEtlContent(DeflaterUtils.unzipString(detailFile.getEtlContent()));
        }
        if(StringUtils.isNotBlank(detailFile.getContent())){
            detailFile.setContent(DeflaterUtils.unzipString(detailFile.getContent()));
        }
        if(StringUtils.isNotBlank(detailFile.getMetaTableContent())){
            detailFile.setMetaTableContent(DeflaterUtils.unzipString(detailFile.getMetaTableContent()));
        }
//        SourceConfig sourceConfig = detailFile.getSourceConfig();
        String sourceContent = resetSourceConfigName(detailFile.getSourceContent());
        String dataStreamConfig = resetDataStreamConfigName(detailFile.getDataStreamContent());
        // 重设 sourceContent 中的 name
        detailFile.setDataStreamContent(dataStreamConfig);
        detailFile.setSourceContent(sourceContent);
        result.ok();
        result.setData(detailFile);
        return result;
    }

    private String resetDataStreamConfigName(String dataStreamConfig) {
        DataStreamConfig ds = JSONUtils.parseObject(dataStreamConfig, DataStreamConfig.class);
        MainResourceFile mainResourceFile = mainResourceFileServiceImpl.getMapper().selectById(ds.getMainResourceId());
        ds.setName(mainResourceFile.getName());
        return JSONUtils.toJsonString(ds);
    }

    private String resetSourceConfigName(String sourceContent) {
        SourceConfig sourceConfig = JSONUtils.parseObject(sourceContent, SourceConfig.class);
        List<SourceConfig.MainResourceDepend> jarDependList = sourceConfig.getJarDependList();
        List<Long> mainResourceDependIdList = jarDependList.stream().map(SourceConfig.MainResourceDepend::getMainResourceId).collect(Collectors.toList());
        Map<Long, MainResourceFile> longStringMap = mainResourceFileServiceImpl.getMapper().batchGetMainResourceDependNameMap(mainResourceDependIdList);
        jarDependList.forEach(
            jarDepend->{
                try {
                    String name = longStringMap.get(jarDepend.getMainResourceId()).getName();
                    jarDepend.setName(name);
                } catch (NullPointerException e) {
                    logger.error("扩展资源[{}]已经丢失",jarDepend.getMainResourceId());
                }

            }
        );
        return JSONUtils.toJsonString(sourceConfig);
    }

    @Transactional
    public Result<Object> deleteFile(FileDto fileDto) {
        Result<Object> result = new Result<>();
        // 先判断 是否有上线任务
        Job job = jobServiceImpl.getMapper().queryJobByFileId(fileDto.getId());
        if (Asserts.isNotNull(job)) {
            putMsg(result,Status.FILE_BE_BOUNDED_WITH_JOB,job.getJobName());
            return result;
        }
        // 如果当前是测试环境，生产环境下已经有该任务，也不能删除
//        if (EnvType.TEST.equals(envType.get()) && Asserts.isNotNull(getMapper().selectById(fileDto.getId()))) {
//            File paramFile = new File();
//            paramFile.setEnabledFlag(Flag.YES.getCode());
//            paramFile.setFileName(fileDto.getFileName());
//            paramFile.setProjectId(fileDto.getProjectId());
//            List<File> fileList = selectAll(paramFile);
//            if (CollectionUtils.isNotEmpty(fileList)) {
//                putMsg(result,Status.FILE_BE_PUBLISH_TO_PROD);
//                return result;
//            }
//        }
        // 先删除file 部分
        this.getMapper().deleteById(fileDto.getId());
        // 然后删除所有tag
        fileTagServiceImpl.getMapper().deleteByFileId(fileDto.getId());
        return result.ok();
    }

    @Transactional
    public Result<Object> updateFile(FileDto fileDto) {
        // 更新文件，
        // 然后检查是否项目能修改
        Result<Object> result = new Result<>();
        // 是否
        if(!allowAddJob(fileDto.getProjectId())){
            putMsg(result,Status.USER_NO_OPERATION_PERM);
            return result;
        }
        File file = getMapper().selectById(fileDto.getId());
        if(!fileDto.getFileName().matches(NAME_REGULAR)){
            putMsg(result,Status.FILE_NAME_NOT_VALID);
            return result;
        }
        // 文件名不一样，如果该项目下已经有其他相同的文件名，就不可以
        if(!file.getFileName().equals(fileDto.getFileName())){
            File otherFile = getMapper().queryFileByName(fileDto.getFileName(), fileDto.getProjectId(), file.getId());
            if(Asserts.isNotNull(otherFile)){
                putMsg(result,Status.FILE_NAME_EXIST_ERROR,otherFile.getFileName());
                return result;
            }
        }
        fileDto.getJobConfig().setFlinkYaml(DeflaterUtils.zipString(fileDto.getJobConfig().getFlinkYaml()));

        if(FileType.DATA_STREAM.equals(FileType.of(file.getFileType()))){
            file.setDataStreamContent(JSONUtils.toJsonString(fileDto.getDataStreamConfig()));
        }
        // jobConfig
        file.setConfigContent(JSONUtils.toJsonString(fileDto.getJobConfig()));
        // resources
        file.setSourceContent(JSONUtils.toJsonString(fileDto.getSourceConfig()));
        // etlContent
        if(StringUtils.isNotBlank(file.getEtlContent())){
            file.setEtlContent(DeflaterUtils.zipString(fileDto.getEtlContent()));
        }
        // metaTableContent
        if(StringUtils.isNotBlank(file.getMetaTableContent())){
            file.setMetaTableContent(DeflaterUtils.zipString(fileDto.getMetaTableContent()));
        }
        // content
        mergeFileMetaAndSqlContent(fileDto,file);
        if(StringUtils.isNotBlank(file.getContent())){
            file.setContent(DeflaterUtils.zipString(file.getContent()));
        }
        result.setData(updateSelective(file)).ok();
        return result;
    }



    /**
     * 检查文件状态
     * @param fileIds
     * @return
     */
    public Result<Object> checkState(List<Long> fileIds) {
        Result<Object> result = new Result<>();
        result.ok();
        if(CollectionUtils.isEmpty(fileIds)){
//            putMsg(result,Status.REQUEST_PARAMS_NOT_VALID_ERROR,"File id list");
            return result;
        }

        // 文件是否上线
        Map<Long, File> longFileMap = getMapper().checkState(fileIds);
        Map<Long, byte[]> onlineFileMap = jobServiceImpl.isOnlineFile(longFileMap.keySet());

        ArrayList<FileDto> fileDtoList = new ArrayList<>();
        longFileMap.forEach((key,value)->{
            FileDto fileDto = new FileDto();
            BeanUtils.copyProperties(value,fileDto);
            if(onlineFileMap.containsKey(key)){
                fileDto.setOnLine(Flag.YES.getCode());
            }
            fileDtoList.add(fileDto);
        });
        return result.setData(fileDtoList);
    }



    /**
     * 查询基础信息
     *
     * @param id
     * @return
     */
    public Result<Object> queryBaseInfo(Long id) {
        Result<Object> result = new Result<>();
        FileDto fileDto = getMapper().queryBaseInfo(id);
        Long folderId = fileDto.getFolderId();
        String strB = assembleFilePath(folderId, new StringBuilder());
        String formatPath = String.format("/作业开发/%s", strB.toString()).replaceAll("/$", "");
        fileDto.setFilePath(formatPath);
        result.setData(fileDto);
        result.ok();
        return result;
    }



    public String assembleFilePath(Long folderId,StringBuilder path) {
        FolderDto folderDto = new FolderDto();
        folderDto.setId(folderId);
        List<FolderDto> folderDtos = folderServiceImpl.getMapper().queryFolder(folderDto);
        if (CollectionUtils.isNotEmpty(folderDtos)) {
            FolderDto leaves = folderDtos.get(0);
            Long parentId = leaves.getParentId();
            String folderName = leaves.getFolderName();
            // 如果还有父节点
            path = new StringBuilder(folderName).append("/").append(path);
            return assembleFilePath(parentId,path);
        }
        return path.toString();
    }

    @Transactional
    public Result<Object> updateBaseInfo(FileDto fileDto) {
        // 更新基础信息
        Result<Object> result = new Result<>();
        Long fileId = fileDto.getId();
        if(Asserts.isNull(fileId)){
            putMsg(result,Status.REQUEST_PARAMS_NOT_VALID_ERROR,"id");
            return result;
        }
        File file = getMapper().selectById(fileId);
        file.setDescription(fileDto.getDescription());
        result.setData(update(file));
        result.ok();
        return result;
    }

    public Result<Object> getAuthedClusters(String clusterType) {
        Result<Object> result = new Result<>();
        User user = ContextUtils.get().getUser();
        List<Cluster> clusterList = clusterServiceImpl.getAuthedCluster(user,clusterType);
        return result.setData(clusterList).ok();
    }

    public Result<Object> allowEditFile(FileDto fileDto) {
        Result<Object> result = new Result<>();
        File file = this.getMapper().selectById(fileDto.getId());
        return result.setData(allowAddJob(file.getProjectId())).ok();
    }

    /**
     * 根据 任务 描述，获取 任务dag
     * @param fileId
     * @return
     */
    public Result<Object> getFileDag(long fileId) {
        Result<Object> result = new Result<>();
        File file = this.getMapper().selectById(fileId);
        String configContent = file.getConfigContent();
        JobConfig jobConfig = JSONUtils.parseObject(configContent, JobConfig.class);
        if(Asserts.isNull(jobConfig)){
            putMsg(result,Status.JOB_CONFIG_NOT_EXIST);
            return result;
        }
        JobConfig.MotorVersion motorVersion = jobConfig.getMotorVersion();
        DataStreamConfig dataStreamConfig = JSONUtils.parseObject(file.getDataStreamContent(), DataStreamConfig.class);
        if(Asserts.isNull(motorVersion)
            || Asserts.isNull(dataStreamConfig)
            || StringUtils.isBlank(dataStreamConfig.getMainClass())
            || Asserts.isNull(dataStreamConfig.getMainResourceId())){
            putMsg(result,Status.DATA_STREAM_CONFIG_NULL_ERROR);
            return result;
        }
        // 如果dag 存在，且是同一个 jar 包版本，则直接返回dag，否则需要重新计算dag
        // if dag exits,and as same as jar version, return dag,else reCalculate dag
        JarFile jarFile = jarFileServiceImpl.getMapper().findMainEffectJar(dataStreamConfig.getMainResourceId());
        if(Asserts.isNull(jarFile)){
            logger.error("Main jar file not exists");
            putMsg(result,Status.JAR_FILE_NOT_EXIST, dataStreamConfig.getMainResourceId());
            return result;
        }
        if(StringUtils.isNotBlank(file.getDag())){
            String[] split = file.getDag().split("---");
            if (split[1].equals(dataStreamConfig.getMainClass()) && split[0].equals(jarFile.getId().toString())) {
                return result.setData(split[2]).ok();
            }
        }
        // request again
        FlinkVersion flinkVersion = flinkVersionServiceImpl.getMapper().selectById(
            motorVersion.getMotorId()
        );
        if(Asserts.isNull(flinkVersion)){
           putMsg(result,Status.FLINK_VERSION_NOT_EXISTS);
           return result;
        }

        // main jar
        JarResource mainJarResource = JarResource
                .builder()
                .jarName(jarFile.getJarName())
                .localParentPath(TaskPathResolver.mainFilePath(file.getProjectId(), file.getId()))
                .remotePath(jarFile.getJarPath())
                .build();;
        List<JarResource> extendedJarResource = null;
        try {
            // get all depended jar
            SourceConfig sourceConfig = JSONUtils.parseObject(file.getSourceContent(), SourceConfig.class);
            if(Asserts.isNull(sourceConfig)){
                putMsg(result,Status.SOURCE_CONFIG_NULL_ERROR);
                return result;
            }

            List<SourceConfig.MainResourceDepend> jarDependList = sourceConfig.getJarDependList();
            extendedJarResource = jarDependList.stream().map(jarDepend -> {
                logger.info("MainResourceId:{}", jarDepend.getMainResourceId().toString());
                logger.info("FlinkVersionId:{}", flinkVersion.getId().toString());
                // connector
                JarFile jarFileDepend;
                if(JarType.FunctionType.CONNECTOR.getValue().equals(jarDepend.getJarFunctionType()) ||
                        JarType.FunctionType.UDF.getValue().equals(jarDepend.getJarFunctionType())
                ){
                    jarFileDepend = jarFileServiceImpl.getMapper().selectByMainResource(
                            jarDepend.getMainResourceId(),
                            flinkVersion.getId()).stream().findFirst().orElse(null);
                } else {
                    jarFileDepend = jarFileServiceImpl.getMapper().findMainEffectJar(
                            jarDepend.getMainResourceId());
                }

                if (Asserts.isNull(jarFileDepend)) {
                    logger.error("Depend resource [{}] not find jar file error.", jarDepend.getMainResourceId());
                    throw new RuntimeException(String.format("Depend resource [%s] not find jar file error.", jarDepend.getMainResourceId()));
                }
                return Optional.of(jarFileDepend).map(jar -> {
                    return JarResource
                            .builder()
                            .jarName(jar.getJarName())
                            .localParentPath(TaskPathResolver.extendedFilePath(file.getProjectId(), file.getId()))
                            .remotePath(jar.getJarPath())
                            .build();
                }).orElse(new JarResource());
            }).collect(Collectors.toList());
        } catch (Exception exception){
            putMsg(result,Status.DEPEND_RESOURCE_HAS_NO_JAR_FILE);
            result.setMsg(ExceptionUtil.exceptionToString(exception));
            return result;
        }
        // create FlinkDagRequest
        FlinkDagRequest flinkDagRequest = FlinkDagRequest.builder()
                .className(dataStreamConfig.getMainClass())
                .args(dataStreamConfig.getAppArgs())
                .mainJarResource(mainJarResource)
                .flinkHomeOptions(
                    FlinkHomeOptions.builder()
                        .flinkPath(flinkVersion.getFlinkPath())
                        .realVersion(flinkVersion.getRealVersion())
                        .build())
                .fileType(FileType.of(file.getFileType()))
                .extendedJarResources(extendedJarResource)
                .build();
        FileDagResponseCommand fileDagResponseCommand = (FileDagResponseCommand) nettyClientService.sendCommand(
                ClusterType.of(jobConfig.getRunClusterInfo().getClusterType()),
                new FileDagCommand(flinkDagRequest),
                FileDagResponseCommand.class);

        if(Asserts.isNull(fileDagResponseCommand) || Asserts.isNull(fileDagResponseCommand.getFlinkDagResponse())){
            putMsg(result,Status.COMMUNICATION_ERROR);
            return result;
        }
        FlinkDagResponse flinkDagResponse = fileDagResponseCommand.getFlinkDagResponse();

        if(flinkDagResponse.getResponseStatus().equals(ResponseStatus.FAIL)){
            putMsg(result,Status.COMMUNICATION_ERROR);
            result.setMsg(flinkDagResponse.getMsg());
            return result;
        }
        String dagText = flinkDagResponse.getDagText();
        if(StringUtils.isBlank(dagText) || !dagText.contains("nodes")){
            putMsg(result,Status.FILE_DAG_CAN_NOT_PARSE_ERROR);
            return result;
        }
        // 设置jar 包Id
        file.setDag(jarFile.getId() + "---" + dataStreamConfig.getMainClass() + "---" + dagText);
        updateSelective(file);
        result.setData(dagText);
        return result.ok();
    }


    @Transactional
    public Result<Object> removeFile(long fileId, long folderId) {
        Result<Object> result = new Result<>();
        File file = this.getMapper().selectById(fileId);
        file.setFolderId(folderId);
        return result.setData(this.getMapper().updateFolderId(fileId,folderId)).ok();
    }

    /**
     * 根据 fileId，jobName，remark 启动文件
     * @param fileId
     * @param jobName
     * @param remark
     * @return
     */
    public Result<Object> onlineFile(Long fileId, String jobName, String remark,Long projectId) {
        // 先创建 tag
        FileTagRto fileTagRto = new FileTagRto();
        fileTagRto.setFileId(fileId);
        // 查找到所有的 tag，然后找到最大的 v 版本，然后
        List<FileTag> fileTags = fileTagServiceImpl.getMapper().selectAllTagByFileId(fileId);
        // 找到 fileTags 中最大的那个值
        Integer maxVersion = VersionUtil.findMaxVersion(fileTags.stream().map(FileTag::getFileTagName).collect(Collectors.toList()));
        String fileTagName = VersionUtil.increaseVersion("v" + maxVersion);
        fileTagRto.setRemark(remark);
        fileTagRto.setFileTagName(fileTagName);
        // 先添加 tag, 找到了相应的 tag 之后
        Result<Object> result = fileTagServiceImpl.addTag(fileTagRto);
        // 根据版本和 FileId 找到相应的 tag
        if(result.isSuccess()){
            FileTag fileTag = fileTagServiceImpl.getMapper().selectTagByFileIdAndVersion(fileId, fileTagName);
            fileTagRto.setJobName(jobName);
            fileTagRto.setFileTagId(fileTag.getId());
        }

        return fileTagServiceImpl.online(fileTagRto, projectId);
    }
}
