package com.dpline.dao.rto;

import com.dpline.dao.entity.Cluster;

public class ClusterRto extends GenericRto<Cluster> {

    public ClusterRto() {
        setVo(new Cluster());
    }

    public Cluster getCluster() {
        return (Cluster) getVo();
    }

    public void setCluster(Cluster vo) {
        setVo(vo);
    }

}
