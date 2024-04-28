package com.dpline.console.service.impl;

import com.dpline.common.Constants;
import com.dpline.common.enums.*;
import com.dpline.common.util.*;
import com.dpline.console.service.GenericService;
import com.dpline.console.util.ContextUtils;
import com.dpline.dao.dto.MainResourceRto;
import com.dpline.dao.entity.JarFile;
import com.dpline.dao.entity.MainResourceFile;
import com.dpline.dao.entity.User;
import com.dpline.dao.generic.GenericMapper;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.mapper.MainResourceFileMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MainResourceFileServiceImpl extends GenericService<MainResourceFile, Long> {

    @Autowired
    JarFileServiceImpl jarFileServiceImpl;

    public MainResourceFileServiceImpl(GenericMapper<MainResourceFile, Long> genericMapper) {
        super(genericMapper);
    }

    public MainResourceFileMapper getMapper() {
        return (MainResourceFileMapper) this.genericMapper;
    }

    public Result<Object> listMainResource(MainResourceRto mainResourceDto) {
        Result<Object> result = new Result<>();
        if (mainResourceDto.getVo().getJarAuthType().equals(JarType.AuthType.PUBLIC.getValue())) {
            mainResourceDto.getVo().setProjectId(null);
        }

        Pagination<MainResourceFile> instanceFromRto = Pagination.getInstanceFromRto(mainResourceDto);
        this.executePagination(x -> this.getMapper().listResource(x), instanceFromRto);
        return result.setData(instanceFromRto).ok();
    }

    public Result<Object> deleteWholeResource(MainResourceRto mainResourceDto) {
        User user = ContextUtils.get().getUser();
        Result<Object> result = new Result<>();
        MainResourceFile mainResourceFile = this.getMapper().selectById(mainResourceDto.getMainResourceId());
        // 如果是 public 且用户 不是 admin, 则无权限
        if(JarType.AuthType.PUBLIC.getValue().equals(mainResourceFile.getJarAuthType()) && UserType.ADMIN_USER.getCode() != user.getIsAdmin()){
            putMsg(result,Status.USER_NO_OPERATION_PERM);
            return result;
        }
        // 查询下面的所有jar包
        List<JarFile> jarFiles = jarFileServiceImpl.getMapper().selectByMainId(mainResourceFile.getId());
        Long boundedTask = beBoundedByTask(jarFiles);
        // TODO 是否改为名称
        if(boundedTask != 0L){
            putMsg(result,Status.JAR_FILE_BOUNDED_ERROR, boundedTask.toString());
            return result;
        }
        // 如果是空的
        if(CollectionUtils.isNotEmpty(jarFiles)){
            List<Long> idList = jarFiles.stream().map(JarFile::getId).collect(Collectors.toList());
//            Long[] objects = (Long[]) idList.toArray();
            jarFileServiceImpl.getMapper().deleteBatchIds(idList);
            jarFiles.forEach(
                jarFile ->{
                    jarFileServiceImpl.deleteRemotePath(jarFile.getJarPath());
                });
        }
        // 最后删除主记录
        this.getMapper().deleteById(mainResourceFile.getId());
        return result.ok();
    }

    private Long beBoundedByTask(List<JarFile> jarFiles) {
        return 0L;
    }

//
//    @Transactional
//    public Result<Object> addJar(MultipartFile file,
//                                 String projectId,
//                                 String jarName,
//                                 String name,
//                                 String description,
//                                 String jarFunctionType, // MAIN | UDF | CONNECTOR | EXTENDED
//                                 String fileMd5,
//                                 String jarAuthType, // PUBLIC | PROJECT
//                                 String type,
//                                 String motorVersionId,
//                                 String runMotorType,
//                                 Long mainResourceId
//    ) {
//        Result<Object> result = new Result<>();
//        User user = ContextUtils.get().getUser();
//        // 解码URL
//        try{
//            jarName = URLDecoder.decode(jarName, "UTF-8");
//            name = URLDecoder.decode(name, "UTF-8");
//            description = URLDecoder.decode(description, "UTF-8");
//        } catch (Exception e){
//            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR);
//            return result;
//        }
//
////        Long pid = StringUtils.isNotEmpty(projectId) ? Long.parseLong(projectId) : null;
//        Optional<JarType.FunctionType> functionTypeOptional = JarType.FunctionType.of(jarFunctionType);
//        if (!functionTypeOptional.isPresent()) {
//            putMsg(result, Status.JAR_FUNCTION_TYPE_ERROR);
//            return result;
//        }
//        Optional<JarType.AuthType> authTypeOptional = JarType.AuthType.of(jarAuthType);
//        if (!authTypeOptional.isPresent()) {
//            putMsg(result, Status.JAR_AUTH_TYPE_ERROR, jarAuthType);
//            return result;
//        }
//        // 如果公共资源不是admin权限，则判断为无权限
//        // 如果不是公共资源，没有项目编码，也判断无权限
//        if (UserType.ADMIN_USER.getCode() != user.getIsAdmin() && JarType.AuthType.PUBLIC.equals(authTypeOptional.get())) {
//            putMsg(result, Status.USER_NO_OPERATION_PERM);
//            return result;
//        }
//        // 如果是项目资源，没有项目Id，则参数报错
//        if (JarType.AuthType.PROJECT.equals(authTypeOptional.get()) && Asserts.isNull(pid)) {
//            putMsg(result, Status.PROJECT_ID_NOT_EXIST);
//            return result;
//        }
//        if(!validateJar(file, jarName)){
//            putMsg(result, Status.JAR_NAME_ERROR, file.getName());
//            return result;
//        }
//        if(existSameNameJar(name,authTypeOptional.get().getValue(),pid,
//            functionTypeOptional.get().getValue())){
//            putMsg(result, Status.JAR_FILE_SAME_NAME_ERROR, file.getName());
//            return result;
//        }
//
//        // 先去判断是否
//        jarFileServiceImpl.addJar(file,projectId,jarName,fileMd5,type,mainResourceId);
//
//
//
//        return result.ok();
//    }



    /**
     * public 公共资源，jarFunctionType 相同 jar 包类型 ，不能存在同一个名字
     * project 私有资源 ，同一个项目下，相同的jarFunctionType ，不能存在同一个名字
     *
     * @return
     */
    private boolean existSameNameMainFile(MainResourceRto mainResourceRto) {
        List<MainResourceFile> mainResourceFiles = this.getMapper().searchSourceByName(mainResourceRto.getName(),
            mainResourceRto.getJarAuthType(),
            mainResourceRto.getJarFunctionType(),
            mainResourceRto.getProjectId());
        return !CollectionUtils.isEmpty(mainResourceFiles);
    }

    public Result<Object> createNewMainFile(MainResourceRto mainResourceRto) {
        Result<Object> result = new Result<>();
        try {
            mainResourceRto.setName(URLDecoder.decode(mainResourceRto.getName(), Constants.UTF_8));
            mainResourceRto.setDescription(URLDecoder.decode(mainResourceRto.getDescription(), Constants.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(existSameNameMainFile(mainResourceRto)){
           putMsg(result,Status.JAR_FILE_SAME_NAME_ERROR,mainResourceRto.getName());
           return result;
        }
        MainResourceFile mainResourceFile = new MainResourceFile();
        BeanUtils.copyProperties(mainResourceRto,mainResourceFile);
        return result.setData(insert(mainResourceFile)).ok();
    }

    public Result<Object> updateMainFile(MainResourceRto mainResourceRto) {
        Result<Object> result = new Result<>();
        // 如果更新的话，需要 查到原来的数据
        MainResourceFile mainResourceFile = this.getMapper().selectById(mainResourceRto.getMainResourceId());
        // 如果已经存在相同名称的
        boolean canCreate = false;
        try {
            mainResourceRto.setName(URLDecoder.decode(mainResourceRto.getName(), Constants.UTF_8));
            mainResourceRto.setDescription(URLDecoder.decode(mainResourceRto.getDescription(), Constants.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(mainResourceRto.getName().equals(mainResourceFile.getName()) && mainResourceRto.getJarFunctionType().equals(mainResourceFile.getJarFunctionType())){
            // 直接插入
            canCreate = true;
        }
        if(!canCreate && !existSameNameMainFile(mainResourceRto)){
            canCreate = true;
        }

        if (canCreate) {
            mainResourceFile.setDescription(mainResourceRto.getDescription());
            mainResourceFile.setName(mainResourceRto.getName());
            mainResourceFile.setJarFunctionType(mainResourceRto.getJarFunctionType());
            mainResourceFile.setRunMotorType(mainResourceRto.getRunMotorType());
            update(mainResourceFile);
        }
        return result.ok();
    }


    public Result<Object> searchSource(MainResourceRto mainResourceRto) {
        Result<Object> result = new Result<>();
        if(StringUtils.isBlank(mainResourceRto.getJarFunctionType())){
            putMsg(result,Status.JAR_FUNCTION_TYPE_ERROR);
            return result;
        }
        Optional<JarType.FunctionType> functionType = JarType.FunctionType.of(mainResourceRto.getJarFunctionType());
        if (functionType.isPresent()){
            return result.setData(
                this.getMapper()
                    .searchSourceByJarName(
                        functionType.get().getValue(),
                        mainResourceRto.getProjectId(),
                        mainResourceRto.getName())
            ).ok();
        }
        putMsg(result,Status.JAR_FUNCTION_TYPE_ERROR);
        return result;
    }


}
