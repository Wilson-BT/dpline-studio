package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.entity.MainResourceFile;
import com.dpline.dao.generic.GenericMapper;
import com.dpline.dao.generic.Pagination;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@DS("mysql")
public interface MainResourceFileMapper extends GenericMapper<MainResourceFile,Long> {

    List<MainResourceFile> listResource(Pagination<MainResourceFile> x);

    List<MainResourceFile> searchSourceByName(@Param("name") String name,
                                           @Param("jarAuthType") String jarAuthType,
                                           @Param("jarFunctionType") String jarFunctionType,
                                           @Param("projectId") Long projectId);

    List<MainResourceFile> searchSourceByJarName(@Param("jarFunctionType") String jarFunctionType,
                                              @Param("projectId") Long projectId,
                                              @Param("name") String name);

    @MapKey("id")
    Map<Long,MainResourceFile> batchGetMainResourceDependNameMap(@Param("mainResourceDependIdList") List<Long> mainResourceDependIdList);
}
