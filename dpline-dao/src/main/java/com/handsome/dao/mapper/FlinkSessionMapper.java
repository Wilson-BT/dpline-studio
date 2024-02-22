package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.handsome.dao.entity.FlinkSession;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FlinkSessionMapper extends BaseMapper<FlinkSession> {


    List<FlinkSession> queryByk8sNameSpaceId(@Param("k8sNameSpaceId") long k8sNameSpaceId);


    Boolean existSameKubernetesClusterId(@Param("k8sNamespaceId") long k8sNamespaceId,
                                         @Param("kubernetesClusterId") String kubernetesClusterId);

    Boolean existSameFlinkSessionName(
            @Param("userId") int userId,
            @Param("flinkSessionName") String flinkSessionName);


    List<FlinkSession> queryAllOnlineFlinkSession();
}
