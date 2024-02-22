package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.handsome.dao.entity.FlinkTagTaskResRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface FlinkTagTaskResRelationMapper extends BaseMapper<FlinkTagTaskResRelation> {


    void batchInsert(@Param("flinkTaskResourceRelations") List<FlinkTagTaskResRelation> flinkTaskResourceRelation);

    List<FlinkTagTaskResRelation> selectAllById(@Param("id") long id);

    void batchDeleteByIdAndResId(@Param("id")long id,
                                 @Param("resIds") Set<Integer> resIds,
                                 @Param("draftTagType") int draftTagType);
    List<FlinkTagTaskResRelation> queryByResId(@Param("resourceIds") List<Integer> allChildren);
}
