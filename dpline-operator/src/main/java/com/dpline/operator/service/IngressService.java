package com.dpline.operator.service;

import com.dpline.operator.job.ClusterFlushEntity;

/**
 * 整合 ingress 实现
 *
 */
public interface IngressService {

    /**
     * 新增ingress
     *
     * @param clusterFlushEntity
     */
    void addIngressRule(ClusterFlushEntity clusterFlushEntity) throws Exception;

    /**
     * 清理ingress
     * @return
     */
    boolean clearIngressRule(ClusterFlushEntity clusterFlushEntity);


}
