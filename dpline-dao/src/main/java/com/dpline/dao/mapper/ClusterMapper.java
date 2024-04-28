package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.entity.Cluster;
import com.dpline.dao.generic.GenericMapper;
import com.dpline.dao.generic.Pagination;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@DS("mysql")
@Repository
public interface ClusterMapper extends GenericMapper<Cluster, Long> {

    List<Cluster> list(Pagination<Cluster> pagination);

    List<Cluster> queryAuthedCluster(@Param("userCode") String userCode,
                                    @Param("clusterType") String clusterType);

    List<Cluster> queryOnlineCluster(@Param("clusterType") String clusterType);


    List<Cluster> selectByClusterName(@Param("clusterName") String clusterName);

    List<Cluster> selectByClusterParams(@Param("clusterParams") String clusterParams);

    List<Cluster> selectByClusterParamsAndId(@Param("id") Long id,
                                             @Param("clusterParams") String clusterParams);
}
