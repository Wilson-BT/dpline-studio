package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.entity.DockerImage;
import com.dpline.dao.generic.GenericMapper;
import com.dpline.dao.generic.Pagination;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DS("mysql")
public interface DockerImageMapper extends GenericMapper<DockerImage,Long> {

    Boolean existSameImageName(@Param("shortName") String shortName);

    List<DockerImage> queryAllImages(Pagination<DockerImage> page);

    List<DockerImage> listDockerImage(@Param("motorVersionId")long motorVersionId,
                                      @Param("motorType") String motorType
                                      );

}
