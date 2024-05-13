package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dpline.dao.entity.FlinkSession;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@DS("mysql")
@Repository
public interface FlinkSessionMapper extends BaseMapper<FlinkSession> {


    List<FlinkSession> queryByk8sNameSpaceId(@Param("k8sNameSpaceId") long k8sNameSpaceId);


//    Boolean existSameKubernetesClusterId(@Param("k8sNamespaceId") long k8sNamespaceId,
//                                         @Param("clusterId") String kubernetesClusterId);

    Boolean existSameFlinkSessionName(
            @Param("userId") int userId,
            @Param("flinkSessionName") String flinkSessionName);


    List<FlinkSession> queryAllOnlineFlinkSession();
}
