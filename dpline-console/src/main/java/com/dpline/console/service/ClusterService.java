package com.dpline.console.service;

import com.dpline.common.util.Result;
import com.dpline.dao.entity.Cluster;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.rto.ClusterRto;

public interface ClusterService {

    /**
     * create
     *
     * @param cluster
     * @return
     */
    Result<Object> create(Cluster cluster);

    /**
     * 删除
     *
     * @param cluster
     * @return
     */
    Result<Object> delete(Cluster cluster);

    /**
     * 更新状态
     *
     * @param cluster
     * @return
     */
    Result<Object> updateState(Cluster cluster);

    /**
     * 更新
     *
     * @param cluster
     * @return
     */
    Result<Object> updateInfo(Cluster cluster);

    /**
     * 分页查询engine列表
     *
     * @param clusterRto
     * @return
     */
    Pagination<Cluster> list(ClusterRto clusterRto);

    Result<Object> selectClusterById(Long clusterId);
}
