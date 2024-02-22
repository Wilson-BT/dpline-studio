package com.dpline.console.service;

import com.dpline.common.util.Result;
import com.dpline.dao.entity.ClusterUser;

import java.util.List;

public interface ClusterUserService {

    /**
     * 分页查询engineU列表
     *
     * @param clusterUser
     * @return
     */
    List<ClusterUser> list(ClusterUser clusterUser);

    /**
     * 增加引擎关联用户
     *
     * @param clusterUser
     * @return
     */
    Result<Object> addClusterUser(List<ClusterUser> clusterUser);

    /**
     * 删除引擎关联用户
     *
     * @param clusterUser
     * @return
     */
    Integer deleteClusterUser(ClusterUser clusterUser);
}
