package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.handsome.dao.entity.FlinkTagTaskUdfRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface FlinkTagTaskUdfRelationMapper extends BaseMapper<FlinkTagTaskUdfRelation> {

    void batchInsert(@Param("flinkTaskUdfRelations") List<FlinkTagTaskUdfRelation> flinkTaskUdfRelation);

    List<FlinkTagTaskUdfRelation> selectAllById(@Param("id") long id);


    void batchDeleteByIdAndUdfId(@Param("id")long id,
                                 @Param("udfIds") Set<Integer> udfIds,
                                 @Param("draftTagType") int draftTagType);
}
