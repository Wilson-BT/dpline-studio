package com.dpline.console.service.impl;

import com.dpline.common.Constants;
import com.dpline.common.enums.*;
import com.dpline.common.util.*;
import com.dpline.console.exception.ServiceException;
import com.dpline.console.service.GenericService;
import com.dpline.console.util.ContextUtils;
import com.dpline.common.minio.Minio;
import com.dpline.dao.dto.JarFileDto;
import java.net.URLDecoder;

import com.dpline.dao.entity.JarFile;
import com.dpline.dao.entity.Job;
import com.dpline.dao.entity.User;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.mapper.JarFileMapper;
import io.minio.StatObjectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JarFileServiceImpl extends GenericService<JarFile, Long> {

    @Autowired
    Minio minio;

    @Autowired
    JobServiceImpl jobServiceImpl;

    private Pattern pattern = Pattern.compile("([A-Za-z0-9_\\-.)(])+");
    // upload/jar/public/主资源ID/jarId/jarName
    //           /projectId/主资源ID/jarId/jarName
    private static String UPLOAD_PATH = "upload/jar/{0}/{1}/{2}/{3}";

    private static String TMP_PATH = "/tmp/dpline/{0}";


    public JarFileServiceImpl(@Autowired JarFileMapper jarFileMapper) {
        super(jarFileMapper);
    }

    public JarFileMapper getMapper() {
        return (JarFileMapper) genericMapper;
    }

//    /**
//     * 将 数据置为空
//     *
//     * @param jarFileDto
//     */
//    private Integer unsetPrimaryJarVersion(JarFileDto jarFileDto) {
//        return getMapper().unsetPrimaryJarVersion(jarFileDto);
//    }

    /**
     * @param file
     * @param jarName
     * @param description
     * @return
     */
    @Transactional
    public Result<Object> addJar(MultipartFile file,
                                 String jarName,
                                 String fileMd5,
                                 String description,
                                 Long motorVersionId,
                                 String jarAuthType,
                                 Long projectId,
                                 Long mainResourceId
    ) {

        Result<Object> result = new Result<>();
        User user = ContextUtils.get().getUser();
        // 解码URL
        try{
            jarName = URLDecoder.decode(jarName, Constants.UTF_8);
            description = URLDecoder.decode(description, Constants.UTF_8);
        } catch (Exception e){
            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR);
            return result;
        }

        // 如果公共资源不是admin权限，则判断为无权限
        // 如果不是公共资源，没有项目编码，也判断无权限
        if (UserType.ADMIN_USER.getCode() != user.getIsAdmin() && JarType.AuthType.PUBLIC.getValue().equals(jarAuthType)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }

        // 如果是项目资源，没有项目Id，则参数报错
        if (JarType.AuthType.PROJECT.getValue().equals(jarAuthType) && Asserts.isNull(projectId)) {
            putMsg(result, Status.PROJECT_ID_NOT_EXIST);
            return result;
        }
        // 目前只支持 jar 包
        if(!validateJar(file, jarName)){
            putMsg(result, Status.JAR_NAME_ERROR, file.getName());
            return result;
        }
        // 查看 版本是否已经存在
        // MAIN 无版本概念
        if(existSameVersionJar(mainResourceId,motorVersionId)){
            putMsg(result, Status.JAR_FILE_SAME_VERSION_EXIST);
            return result;
        }
        try {
            long jarId = CodeGenerateUtils.getInstance().genCode();
            Optional<JarType.AuthType> authTypeOption = JarType.AuthType.of(jarAuthType);
            if(!authTypeOption.isPresent()){
                throw new RuntimeException("AuthType column is not exits.");
            }
            // 然后上传到S3
            String uploadPath = uploadJar(file, authTypeOption.get(), projectId, mainResourceId, jarName,String.valueOf(jarId));
            if (StringUtils.isEmpty(uploadPath)) {
                putMsg(result,Status.JAR_UPLOAD_FAILED);
                return result;
            }
            logger.info("jar path: [{}], jarName: [{}]", uploadPath, jarName);
            // 如果是重新上传，先把主版本置为0，再把当前版本置为1
            JarFile jarFile = new JarFile();
            jarFile.setId(jarId);
            jarFile.setJarName(jarName);
            jarFile.setJarPath(uploadPath);
            jarFile.setFileMd5(fileMd5);
            jarFile.setDescription(description);
            jarFile.setMainResourceId(mainResourceId);
            jarFile.setMotorVersionId(motorVersionId);
            insertSelective(jarFile);
        } catch (Exception e){
            logger.error(e.toString());
            putMsg(result,Status.JAR_UPLOAD_FAILED);
            return result;
        }
        return result.ok();
    }

//    /**
//     * public 公共资源，jarFunctionType 相同 jar 包类型 ，不能存在同一个名字
//     * project 私有资源 ，同一个项目下，相同的jarFunctionType ，不能存在同一个名字
//     *
//     * @param name
//     * @param projectId
//     * @param jarFunctionType
//     * @return
//     */
//    private boolean existSameNameJar(String name,String jarAuthType, Long projectId,String jarFunctionType) {
//        List<JarFile> jarFiles = this.getMapper().searchJarByName(name,jarAuthType,jarFunctionType,projectId);
//        return !CollectionUtils.isEmpty(jarFiles);
//    }

    /**
     * @param file        上传文件
     * @param jarAuthType
     * @param projectId
     * @param jarName
     * @return
     */
    @Transactional
    public String uploadJar(MultipartFile file,
                             JarType.AuthType jarAuthType,
                             Long projectId,
                             Long mainResourceId,
                             String jarName,
                             String jarId
                             ) {
        long start = System.currentTimeMillis();
        String uploadPath = assemblePath(jarAuthType, projectId.toString(), mainResourceId.toString(), jarId,jarName);
        logger.info("上传jar 路径===" + uploadPath);
        try (InputStream inputStream = file.getInputStream()) {
            minio.putObject(uploadPath, inputStream);
            logger.info("上传jar包耗时===" + (System.currentTimeMillis() - start));
            return uploadPath;
        } catch (Exception e) {
            logger.error("jar包[{}]上传失败", jarName);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 拼接 路径
     * 9
     *
     * @param jarAuthType
     * @param projectId
     * @param jarName
     * @return
     */
    // 拼接文件的路径
    // 公共资源
    // upload/jar/public/jarId/JarName
    // 项目资源
    // upload/jar/projectId/jarId/JarName
    private String assemblePath(JarType.AuthType jarAuthType,
                                String projectId,
                                String mainResourceId,
                                String jarId,
                                String jarName
                                ) {
        if (jarAuthType.equals(JarType.AuthType.PROJECT)) {
            if(Asserts.isNull(projectId)){
                throw new ServiceException(Status.PROJECT_ID_NOT_EXIST);
            }
            return MessageFormat.format(UPLOAD_PATH, projectId, mainResourceId, jarId, jarName);
        }
        return MessageFormat.format(UPLOAD_PATH, "public", mainResourceId, jarId,jarName);
    }

    private String genericLocalJarPath(JarType.AuthType jarAuthType,
                                       Long projectId,
                                       Long mainResourceId,
                                       String jarName,
                                       String jarId) {
        return MessageFormat.format(TMP_PATH, assemblePath(jarAuthType, projectId.toString(), mainResourceId.toString(),jarId,jarName));
    }

    private String genericLocalJarPath(String uploadPath) {
        return MessageFormat.format(TMP_PATH, uploadPath);
    }



    /**
     * @param file    校验文件的真实性
     * @param jarName
     * @return
     */
    public boolean validateJar(MultipartFile file, String jarName) {
        String fileName = file.getOriginalFilename();
        if (Asserts.isNull(fileName)) {
            return false;
        }
        Matcher matcher = pattern.matcher(fileName);
        if (!matcher.matches()) {
            return false;
        }
        if (!fileName.endsWith(".jar")) {
            return false;
        }
        if (!fileName.equals(jarName)){
            return false;
        }
        return true;
    }

    /**
     * 通过motorVersion 或者
     *
     * @return
     */
    public Result<Object> queryJar(Long mainResourceId,Long motorVersionId) {
        Result<Object> result = new Result<>();
        // 找到 jar 包
        return result.setData(this.getMapper().selectByMainResource(mainResourceId,motorVersionId)).ok();
    }

//    /**
//     * 查询
//     * @param jarFileDto
//     */
//    public Result<Object> queryJarReferenceJobs(JarFileDto jarFileDto) {
//        Result<Object> result = new Result<>();
//        List<Job> jobList = this.getMapper().queryJarReferenceJobs(jarFileDto);
//        return result.setData(new PageInfo<>(jobList)).ok();
//    }

    /**
     * 更新jar包
     * 如果 primaryJar 是1，那么如果从
     * @param jarFileDto
     * @return
     */
    @Transactional
    public Result<Object> updateJar(JarFileDto jarFileDto) {
        Result<Object> result = new Result<>();
        if(Asserts.isNull(jarFileDto.getId())){
            putMsg(result,Status.REQUEST_PARAMS_NOT_VALID_ERROR,"id");
            return result;
        }

        JarFile jarFile = this.getMapper().listAllMessageById(jarFileDto.getId());
        if (Asserts.isNull(jarFile)){
            putMsg(result,Status.JAR_FILE_NOT_EXIST,jarFileDto.getJarName());
            return result;
        }
        // 如果是 UDF 或者 CONNECTOR,必须存在 引擎版本
        if(jarFile.getJarFunctionType().equals(JarType.FunctionType.CONNECTOR.getValue())
            || jarFile.getJarFunctionType().equals(JarType.FunctionType.UDF.getValue())){
            if(Asserts.isNull(jarFileDto.getMotorVersionId())){
                putMsg(result,Status.JAR_FILE_VERSION_NOT_EXIST);
                return result;
            }
            // 引擎版本改变，但是已经存在相同版本，则报错
        }
        if(!jarFileDto.getMotorVersionId().equals(jarFile.getMotorVersionId())
            && existSameVersionJar(jarFileDto.getMainResourceId(),jarFileDto.getMotorVersionId())){
            putMsg(result,Status.JAR_FILE_SAME_VERSION_EXIST);
            return result;
        }
        // 引擎 版本 改变，如果已经存在其他的版本，报错，如果没有版本的话
        jarFile.setMotorVersionId(jarFileDto.getMotorVersionId());
        jarFile.setDescription(jarFileDto.getDescription());

        return result.setData(updateSelective(jarFile)).ok();
    }

    private boolean existSameVersionJar(Long mainResourceId, Long motorVersionId) {
        List<JarFile> jarFiles= this.getMapper().selectByMainResource(mainResourceId, motorVersionId);
        return CollectionUtils.isNotEmpty(jarFiles);
    }


    /**
     * 下载jar包
     *
     * @param jarFileDto
     * @return
     */
    public ResponseEntity downloadJar(JarFileDto jarFileDto) {
        JarFile jarFile = this.getMapper().selectById(jarFileDto.getId());
        InputStream inputStream = null;
        try {
            StatObjectResponse objectInfo = minio.getObjectInfo(minio.getBucketName(), jarFile.getJarPath());
            inputStream = minio.getObject(jarFile.getJarPath());
            String fileName = FileUtils.extractFileName(jarFile.getJarPath());
            logger.error("已经获取到文件[{}]，载入服务器，准备下载到本地",fileName);
            // 写入本地文件
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(objectInfo.size())
                .contentType(MediaType.parseMediaType("application/java-archive"))
                .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            try {
                if(inputStream != null){
                    inputStream.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            logger.error("文件下载失败。。。");
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public Result<Object> deleteJar(JarFileDto jarFileDto) {
        Result<Object> result = new Result<>();
        // 先判断是否有权限
        JarFile jarFile = this.getMapper().listAllMessageById(jarFileDto.getId());
        if(Asserts.isNull(jarFile)){
            putMsg(result,Status.JAR_FILE_NOT_EXIST,jarFileDto.getId());
            return result;
        }
        User user = ContextUtils.get().getUser();
        // 公共资源的话，需要 管理员权限
        if(JarType.AuthType.PUBLIC.getValue().equals(jarFile.getJarAuthType()) && user.getIsAdmin() != UserType.ADMIN_USER.getCode()){
            putMsg(result,Status.USER_NO_OPERATION_PERM);
            return result;
        }
        // 如果是Main资源包，需要判断是否存在 job 依赖
        if(JarType.FunctionType.MAIN.getValue().equals(jarFile.getJarFunctionType()) || JarType.FunctionType.EXTENDED.getValue().equals(jarFile.getJarFunctionType())){
            List<Job> jobList = jobServiceImpl.getMapper().selectByMainResourceId(jarFile.getMainResourceId());
            if(CollectionUtils.isNotEmpty(jobList)){
                putMsg(result,Status.JAR_FILE_BOUNDED_ERROR,jobList.get(0).getJobName());
            }
        } else {
            List<Job> jobList = jobServiceImpl.getMapper().selectByResourceId(jarFile.getMainResourceId(),jarFile.getMotorVersionId());
            if(CollectionUtils.isNotEmpty(jobList)){
                putMsg(result,Status.JAR_FILE_BOUNDED_ERROR,jobList.get(0).getJobName());
            }
        }
        // 需要知道是否已经被绑定了，
        try {
            minio.removeObject(jarFile.getJarPath());
            result.setData(this.getMapper().deleteById(jarFileDto.getId())).ok();
            return result;
        } catch (Exception e) {
            logger.error("线上Jar包移除失败");
            e.printStackTrace();
        }
        putMsg(result,Status.JAR_FILE_NOT_EXIST,jarFileDto.getId());
        return result;
    }



    public boolean deleteRemotePath(String jarPath){
        // upload/jar/public/主资源ID/jarId/jarName
        try {
            String versionPath = jarPath.substring(0, jarPath.lastIndexOf("/"));
            String namePath = versionPath.substring(0, versionPath.lastIndexOf("/")+1);
            minio.removeObjects(namePath);
            return true;
        } catch (Exception e){
            logger.error(e.toString());
        }
        return false;
    }

//    public Result<Object> searchJar(MainResourceRto mainResourceRto) {
//        Result<Object> result = new Result<>();
//        if(StringUtils.isBlank(mainResourceRto.getJarFunctionType())){
//            putMsg(result,Status.JAR_FUNCTION_TYPE_ERROR);
//            return result;
//        }
//        Optional<JarType.FunctionType> functionType = JarType.FunctionType.of(mainResourceRto.getJarFunctionType());
//        if (functionType.isPresent()){
//            return result.setData(
//                this.getMapper()
//                    .searchJarByJarName(
//                        functionType.get().getValue(),
//                        mainResourceRto.getProjectId(),
//                        mainResourceRto.getName(),
//                        0L)
//            ).ok();
////            return result.setData(this.getMapper())
//        }
//        putMsg(result,Status.JAR_FUNCTION_TYPE_ERROR);
//        return result;
//    }

    public Result<Object> listJar(JarFileDto jarFileDto) {
        Result<Object> result = new Result<>();
        Pagination<JarFile> instanceFromRto = Pagination.getInstanceFromRto(jarFileDto);
        executePagination(x-> this.getMapper().listJar(x),instanceFromRto);
        return result.setData(instanceFromRto).ok();
    }
}
