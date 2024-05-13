package com.dpline.console.handler;

import cn.hutool.core.util.ArrayUtil;
import com.dpline.common.enums.FileType;
import com.dpline.common.enums.JarType;
import com.dpline.common.enums.ResFsType;
import com.dpline.common.enums.RunMotorType;
import com.dpline.common.store.FsStore;
import com.dpline.common.util.*;
import com.dpline.console.service.impl.JarFileServiceImpl;
import com.dpline.dao.domain.*;
import com.dpline.dao.dto.JobDto;
import com.dpline.dao.entity.JarFile;
import com.dpline.dao.entity.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;


@Deprecated
public class ApplicationDeployHandler implements DeployHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationDeployHandler.class);

    private FsStore fsStore;

    private JarFileServiceImpl jarFileServiceImpl;


    public ApplicationDeployHandler(FsStore fsStore,
                             JarFileServiceImpl jarFileServiceImpl) {
        this.fsStore = fsStore;
        this.jarFileServiceImpl = jarFileServiceImpl;
    }

    public boolean downLoadJars(AbstractDeployConfig abstractDeployConfig, FileType fileType){
        // 创建 路径
        FlinkDeployConfig taskConfig = (FlinkDeployConfig) abstractDeployConfig;
        createLocalPath(taskConfig.mainFilePath());
        createLocalPath(taskConfig.extendedFilePath());
        createLocalPath(taskConfig.sqlFilePath());
        AtomicBoolean downLoadFlag = new AtomicBoolean(true);
        try {
            // 如果是sql模式，sql写入本地文件
            if(fileType.equals(FileType.SQL_STREAM)){
                FileUtils.writeContent2File(taskConfig.getSqlText(), taskConfig.sqlFileAbsoluteName());
            }
            // 下载 connector 包
            if (!taskConfig.getConnectorIdPathMap().isEmpty() && downLoadFlag.get()){
                Map<Long, JarDepend> connectorIdPathMap = taskConfig.getConnectorIdPathMap();
                connectorIdPathMap.values().forEach(jarDepend -> {
                    String jarLocalPath = taskConfig.extendedFilePath() + "/" + jarDepend.getJarName();
                    boolean connector = downLoad(jarDepend.getJarPath(), jarLocalPath, "CONNECTOR");
                    if (!connector){
                        downLoadFlag.set(false);
                    }
                });
            }
            // 下载 udf 包 ，有Flink 版本限制
            if (!taskConfig.getUdfPathMap().isEmpty() && downLoadFlag.get()){
                Map<Long, JarDepend> connectorIdPathMap = taskConfig.getUdfPathMap();
                connectorIdPathMap.values().forEach(jarDepend -> {
                    String jarLocalPath = taskConfig.extendedFilePath() + "/" + jarDepend.getJarName();
                    boolean connector = downLoad(jarDepend.getJarPath(), jarLocalPath, "UDF");
                    if (!connector){
                        downLoadFlag.set(false);
                    }
                });
            }
            // 下载扩展包，无Flink 版本限制
            if (!taskConfig.getExtendIdPathMap().isEmpty() && downLoadFlag.get()){
                Map<Long, JarDepend> extendIdPathMap = taskConfig.getExtendIdPathMap();
                extendIdPathMap.values().forEach(jarDepend -> {
                    String jarLocalPath = taskConfig.extendedFilePath() + "/" + jarDepend.getJarName();
                    boolean extend = downLoad(jarDepend.getJarPath(), jarLocalPath, "EXTEND");
                    if (!extend){
                        downLoadFlag.set(false);
                    }
                });
            }
            // main jar 下载，无Flink 版本限制
            if (!Asserts.isNull(taskConfig.getMainJar()) && downLoadFlag.get()) {
                JarDepend mainJar = taskConfig.getMainJar();
                String jarRemotePath = mainJar.getJarPath();
                String jarLocalPath = taskConfig.mainFilePath() + "/" + mainJar.getJarName();
                fsStore.download(jarRemotePath, jarLocalPath,false);
                boolean main = downLoad(jarRemotePath, jarLocalPath, "MAIN");
                if (!main){
                    downLoadFlag.set(false);
                }
            }
            return downLoadFlag.get();
        } catch (Exception e) {
            logger.error("Local file generate failed, {} \n", e.toString());
        }
        return false;
    }

    /**
     * 将本地文件目录下的所有文件 全部都提交到S3上
     *
     * @return
     */
    private boolean deployLocalDirAndJarsToS3(AbstractDeployConfig abstractDeployConfig) {
        try {
            // 先移除远程目录
            fsStore.delete(abstractDeployConfig.getTaskRemotePath(),true);
            logger.info("Remote path [{}] has been removed.",abstractDeployConfig.getTaskRemotePath());
            // 然后将本地目录 上传到远程地址
            uploadFile(abstractDeployConfig,"Extend");
            uploadFile(abstractDeployConfig,"Main");
            uploadFile(abstractDeployConfig,"Sql");
            return true;
        } catch (Exception e) {
            logger.error("Put file from local path [{}] to S3 path [{}] failed.", abstractDeployConfig.getTaskLocalPath(), abstractDeployConfig.getTaskRemotePath());
            return false;
        }
    }

    /**
     * 将本地扩展包扔到S3,如果本地目录不存在
     */

    private void uploadFile(AbstractDeployConfig abstractDeployConfig,String fileTypeFlag) throws Exception {
        String localPath = "";
        String remotePath = "";
        switch (fileTypeFlag){
            case "Extend":
                localPath = abstractDeployConfig.extendedFilePath();
                remotePath = abstractDeployConfig.extendedRemoteFilePath();
                break;
            case "Main":
                localPath = abstractDeployConfig.mainFilePath();
                remotePath = abstractDeployConfig.mainRemoteFilePath();
                break;
            case "Sql":
                localPath = abstractDeployConfig.sqlFilePath();
                remotePath = abstractDeployConfig.sqlRemoteFilePath();
                break;
            default:
                throw new RuntimeException(String.format("unsupport file type {}",fileTypeFlag));
        }
        File file = new File(localPath);
        if(!file.exists() || ArrayUtil.isEmpty(file.listFiles())){
            fsStore.mkdir(remotePath);
        }
        File[] files = file.listFiles();
        String finalRemotePath = remotePath;
        Arrays.stream(files).forEach(f -> {
            try {
                fsStore.upload(f.getAbsolutePath(),finalRemotePath + "/" + f.getName(),false,true);
                logger.info("[{}] jar => [{}] has been upload success",fileTypeFlag,f.getAbsolutePath());
            } catch (Exception e) {
                logger.error("[{}] jar => [{}] has been upload failed",fileTypeFlag,f.getAbsolutePath());
                e.printStackTrace();
            }
        });
    }


    @Override
    public void clear(Job job) {
        // 删除本地文件目录
        deleteLocalDir(TaskPathResolver.getTaskLocalDeployDir(job.getProjectId(),job.getId()));
        try {
            // 移除 task 部署目录
            fsStore.delete(TaskPathResolver.getTaskRemoteDeployDir(job.getProjectId(),job.getId()),true);
            // 移除ha
            fsStore.delete(TaskPathResolver.getTaskRemoteHaDir(job.getJobName()),true);
            // 移除所有checkpoint
            fsStore.delete(TaskPathResolver.getJobDefaultCheckPointDir(job.getProjectId(),job.getId()),true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * download file
     */
    public String deploy(JobDto jobDto) {
        AbstractDeployConfig abstractDeployConfig = this.convertToDeployConfig(jobDto);
        deleteLocalDir(abstractDeployConfig.getTaskLocalPath());
        logger.info("Local dir [{}] has been delete", abstractDeployConfig.getTaskLocalPath());
        if (downLoadJars(abstractDeployConfig,FileType.of(jobDto.getFileType())) && deployLocalDirAndJarsToS3(abstractDeployConfig)) {
            logger.info("Task deploy success,deploy dir: [{}]", abstractDeployConfig.getTaskRemotePath());
            return abstractDeployConfig.getTaskRemotePath();
        }
        deleteLocalDir(abstractDeployConfig.getTaskLocalPath());
        logger.warn("任务部署失败");
        return null;
    }

    /**
     * delete local dir
     * @param path
     */
    public void deleteLocalDir(String path){
        try {
            File file = new File(path);
            if (file.exists()){
                FileUtils.deleteDirectory(file);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public AbstractDeployConfig convertToDeployConfig(JobDto jobDto) {
        Optional<RunMotorType> runMotorTypeOptional = RunMotorType.of(jobDto.getRunMotorType());
        return runMotorTypeOptional.map(runMotorType -> {
            switch (runMotorType){
                case FLINK:
                    logger.info("正在转换Flink专用配置。。。");
                    return this.convertToFlinkDeployConfig(jobDto, new FlinkDeployConfig());
                default:
                    return null;
            }
        }).orElse(null);
    }

    public AbstractDeployConfig convertToFlinkDeployConfig(JobDto jobDto, FlinkDeployConfig flinkDeployConfig) {
        // 查询 job 的任务
        if(Asserts.isNull(jobDto)){
            return null;
        }
        flinkDeployConfig.setProjectId(jobDto.getProjectId());
        flinkDeployConfig.setJobId(jobDto.getId());
        flinkDeployConfig.setMotorVersionId(jobDto.getMotorVersionId());
        flinkDeployConfig.setFileType(FileType.of(jobDto.getFileType()));
        // 重新解析 flinkYaml文件 为 map格式，并赋予 RunResourceConfig
        if(flinkDeployConfig.getFileType().equals(FileType.SQL_STREAM)){
            flinkDeployConfig.setSqlText(JobConfigSerialization.unzipJobContent(jobDto.getJobContent()));
        }
        // main Jar 设置
        JarFile mainEffectJar = jarFileServiceImpl.getMapper().selectById(jobDto.getMainJarId());
        flinkDeployConfig.setMainJar(new JarDepend(mainEffectJar));
        SourceConfig sourceConfig = JSONUtils.parseObject(jobDto.getSourceContent(), SourceConfig.class);
        if (Asserts.isNull(sourceConfig)){
            return null;
        }
        List<SourceConfig.MainResourceDepend> jarDependList = sourceConfig.getJarDependList();
        jarDependList.forEach(jarDepend -> {
                // connector
            if (JarType.FunctionType.CONNECTOR.getValue().equals(jarDepend.getJarFunctionType())){
                List<JarFile> jarFiles = jarFileServiceImpl.getMapper().selectByMainResource(
                    jarDepend.getMainResourceId(),
                    jobDto.getMotorVersionId());
                if(CollectionUtils.isEmpty(jarFiles)){
                    logger.error("Connector file [{}] is not find error.",jarDepend.getMainResourceId());
                    return;
                }
                JarDepend connectorJarDepend = new JarDepend(jarFiles.get(0));
                flinkDeployConfig.getConnectorIdPathMap().put(jarDepend.getMainResourceId(),connectorJarDepend);
                // udf
            } else if (JarType.FunctionType.UDF.getValue().equals(jarDepend.getJarFunctionType())){
                List<JarFile> jarFiles = jarFileServiceImpl.getMapper().selectByMainResource(
                    jarDepend.getMainResourceId(),
                    jobDto.getMotorVersionId());
                if(CollectionUtils.isEmpty(jarFiles)){
                    logger.error("Udf file [{}] is not find error.",jarDepend.getMainResourceId());
                    return;
                }
                JarDepend udfJarDepend = new JarDepend(jarFiles.get(0));
                flinkDeployConfig.getUdfPathMap().put(jarDepend.getMainResourceId(),udfJarDepend);
            } else {
                // extend
                JarFile jarFile = jarFileServiceImpl.getMapper().findMainEffectJar(
                    jarDepend.getMainResourceId());
                if(Asserts.isNull(jarFile)){
                    logger.error("Extend file [{}] is not find error.",jarDepend.getMainResourceId());
                    return;
                }
                JarDepend extendJarDepend = new JarDepend(jarFile);
                flinkDeployConfig.getExtendIdPathMap().put(jarDepend.getMainResourceId(),extendJarDepend);
            }
        });
        // 设置job 与 mainJar 的关系
        jarFileServiceImpl.getMapper().updateMainJarId(jobDto.getId(), mainEffectJar.getId());
        return flinkDeployConfig;
    }

    private void createLocalPath(String path) {
        try {
            FileUtils.createDir(path, ResFsType.LOCAL);
            logger.info("Path ==> [{}] has been create.",path);
        } catch (IOException e) {
            logger.error("Path ==> [{}] create failed.",path);
            e.printStackTrace();
        }
    }

    public boolean downLoad(String jarRemotePath,String jarLocalPath,String logPrefix){
        try {
            fsStore.download(jarRemotePath, jarLocalPath,false);
            logger.info("{} Jar => [{}] has been download.", logPrefix, jarLocalPath);
            return true;
        } catch (Exception exception) {
            logger.error("{} Jar => [{}] download failed.Remote path [{}]. {}", logPrefix,
                    jarLocalPath,
                    jarRemotePath,
                    ExceptionUtil.exceptionToString(exception));
            return false;
        }
    }



}
