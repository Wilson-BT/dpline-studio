package com.dpline.console.service;


import com.dpline.dao.dto.DataSourceDto;
import com.dpline.dao.entity.DataSource;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.rto.DataSourceRto;


public interface DataSourceService {

    /**
     * 创建
     *
     * @param dataSource
     * @return
     */
    int create(DataSource dataSource);

    /**
     * 测试连接
     *
     * @param dataSourceDto
     * @return
     */
    Boolean checkConnect(DataSourceDto dataSourceDto);

    /**
     * 分页查询datasource列表
     *
     * @param dataSourceRto
     * @return
     */
    Pagination<DataSource> list(DataSourceRto dataSourceRto);
}
