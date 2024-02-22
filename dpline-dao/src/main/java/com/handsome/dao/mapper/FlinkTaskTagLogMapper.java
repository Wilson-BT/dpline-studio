package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.handsome.dao.entity.FlinkTaskTagLog;
import org.apache.ibatis.annotations.Param;

public interface FlinkTaskTagLogMapper extends BaseMapper<FlinkTaskTagLog> {

    Boolean existSameTagNameInSameTask(@Param("taskDefinitionId") long taskDefinitionId,
                              @Param("tagName") String tagName);

    String queryNameByFlinkVersionId(@Param("flink_version_id") int flinkVersionId);

    String queryNameById(@Param("id") long id);

}
