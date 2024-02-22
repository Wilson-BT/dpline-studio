package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.handsome.dao.entity.FlinkTaskDefinition;
import org.apache.ibatis.annotations.Param;


public interface FlinkTaskDefinitionMapper extends BaseMapper<FlinkTaskDefinition> {

    Boolean existSameFlinkTaskName(@Param("taskName") String taskName,
                                      @Param("projectCode") long projectCode);

    FlinkTaskDefinition queryAllByProjectCode(@Param("projectCode")long projectCode);

    String queryNameByFlinkVersionId(@Param("flinkVersionId") int flinkVersionId);

    String queryNameById(@Param("id") long id);

}
