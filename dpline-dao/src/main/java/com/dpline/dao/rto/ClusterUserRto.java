package com.dpline.dao.rto;

import com.dpline.dao.entity.ClusterUser;

public class ClusterUserRto extends GenericRto<ClusterUser> {

    public ClusterUserRto() {
        setVo(new ClusterUser());
    }

    public ClusterUser getClusterUser() {
        return (ClusterUser) getVo();
    }

    public void setClusterUser(ClusterUser vo) {
        setVo(vo);
    }

}
