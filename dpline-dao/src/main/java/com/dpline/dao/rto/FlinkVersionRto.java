package com.dpline.dao.rto;

import com.dpline.dao.entity.FlinkVersion;

public class FlinkVersionRto extends GenericRto<FlinkVersion> {

    public FlinkVersionRto() {
        setVo(new FlinkVersion());
    }

    public FlinkVersion getFlinkVersion() {
        return (FlinkVersion) getVo();
    }

    public void setFlinkVersion(FlinkVersion vo) {
        setVo(vo);
    }

}
