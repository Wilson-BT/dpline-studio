package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.handsome.dao.entity.DockerImage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DockerImageMapper extends BaseMapper<DockerImage> {

    Boolean existSameImageName(@Param("image_name") String imageName);

    List<DockerImage> queryAllImages();

}
