package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.entity.DataSource;
import com.dpline.dao.generic.GenericMapper;
import com.dpline.dao.generic.Pagination;

import java.util.List;

@DS("mysql")
public interface DataSourceMapper extends GenericMapper<DataSource, Long> {

    List<DataSource> list(Pagination<DataSource> pagination);
}
