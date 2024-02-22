package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.handsome.dao.entity.FlinkRunTaskInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;


public interface FlinkTaskInstanceMapper extends BaseMapper<FlinkRunTaskInstance> {

    Boolean existFlinkTaskOnSameInstance(@Param("flinkTaskTagId") long flinkTaskTagId,@Param("flinkInstanceName") String flinkInstanceName);

    String queryTaskInstanceNameByK8sNamespaceId(@Param("k8sNameSpaceId") long k8sNameSpaceId);

    String queryTaskInstanceNameByK8sAndUser(@Param("k8sNameSpaceId") Set<Long> k8sNameSpaceId, @Param("userId") int userId);

    FlinkRunTaskInstance selectByDockerImageId(@Param("flinkImageId") int id);

    List<FlinkRunTaskInstance> selectByFlinkInstanceStatus(@Param("execStatus") List<Integer> status);

    boolean updateExecStatus(@Param("flinkTaskId") long flinkTaskId,@Param("execStatus") int code);

    boolean updateRestUrl(@Param("flinkTaskId") long flinkTaskId,@Param("jobId") String jobId,@Param("restUrl") String restUrl);

    boolean batchUpdateSessionRestUrl(@Param("sessionName") long flinkTaskId,@Param("restUrl") String restUrl);

    FlinkRunTaskInstance selectApplicationInfoById(@Param("flinkTaskId")long flinkTaskId);


}
