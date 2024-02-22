package com.dpline.dao.rto;

import com.dpline.dao.entity.DataSource;

public class DataSourceRto extends GenericRto<DataSource> {

    public DataSourceRto() {
        setVo(new DataSource());
    }

    public DataSource getDataSource() {
        return (DataSource) getVo();
    }

    public void setDataSource(DataSource vo) {
        setVo(vo);
    }

}
