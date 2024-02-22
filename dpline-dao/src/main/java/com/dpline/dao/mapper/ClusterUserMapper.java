package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.entity.ClusterUser;
import com.dpline.dao.generic.GenericMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@DS("mysql")
@Repository
public interface ClusterUserMapper extends GenericMapper<ClusterUser, Long> {

    List<ClusterUser> list(@Param("clusterUser") ClusterUser clusterUser);

    Integer deleteRelationByUserCode(@Param("userCode") String userCode);

    Integer deleteRelationByClusterId(@Param("clusterId") Long clusterId);
}
