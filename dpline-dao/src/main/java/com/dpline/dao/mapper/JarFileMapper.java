package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.entity.JarFile;
import com.dpline.dao.generic.GenericMapper;
import com.dpline.dao.generic.Pagination;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DS("mysql")
public interface JarFileMapper extends GenericMapper<JarFile,Long> {


    List<JarFile> listJar(Pagination<JarFile> jarFile);

//    List<JarFile> searchJar(JarFileDto jarFileDto);

//    String findMaxVersion(JarFileDto jarFileDto);

//    Integer unsetPrimaryJarVersion(JarFileDto jarFileDto);

//    List<JarFile> queryJar(Pagination<JarFile> pagination);

//    List<Job> queryJarReferenceJobs(JarFileDto jarFileDto);

//    Integer deleteJarByName(@Param("jarFileDto") JarFileDto jarFileDto,@Param("jarFunctionType") String jarFunctionType);

//    /**
//     * -- 有projectId，可能是 公共资源，也有可能是PROJECT 资源
//     * -- 没有projectId，只能是 公共资源
//     * @param jarFunctionType
//     * @param projectId
//     * @param name
//     * @return
//     */
//    List<JarFile> searchJarByJarName(@Param("jarFunctionType") String jarFunctionType,
//                                     @Param("projectId") Long projectId,
//                                     @Param("name") String name,
//                                     @Param("motorVersionId") Long motorVersionId
//                                     );
//
//    List<JarFile> searchJarByName(@Param("name") String name,
//                                  @Param("jarAuthType") String jarAuthType,
//                                  @Param("jarFunctionType") String jarFunctionType,
//                                  @Param("projectId") Long projectId);

//    Integer batchUpdateJarName(@Param("oldName") String oldName,
//                               @Param("newName") String newName,
//                               @Param("jarFunctionType") String jarFunctionType,
//                               @Param("jarAuthType") String jarAuthType,
//                               @Param("projectId") Long projectId);

    List<JarFile> selectByMainId(@Param("mainResourceId") Long id);

    JarFile listAllMessageById(@Param("id") Long id);

    List<JarFile> selectByMainResource(@Param("mainResourceId") Long mainResourceId,
                                       @Param("motorVersionId") Long motorVersionId);

    JarFile findMainEffectJar(@Param("mainResourceId") Long mainResourceId);

    Integer updateMainJarId(@Param("jobId") Long jobId,
                            @Param("jarId") Long jarId);
}
