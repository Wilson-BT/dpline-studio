package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.handsome.dao.entity.FlinkVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface FlinkVersionMapper extends BaseMapper<FlinkVersion> {

    Boolean existFlinkVersion(@Param("flinkName")String flinkName,@Param("flinkHomePath") String FlineHomePath);

    List<FlinkVersion> queryAllList(@Param("online")int online);

}
