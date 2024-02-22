package com.dpline.console.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.dpline.common.enums.DataSourceType;
import com.dpline.console.service.DataSourceService;
import com.dpline.console.service.GenericService;
import com.dpline.dao.dto.DataSourceDto;
import com.dpline.dao.entity.DataSource;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.mapper.DataSourceMapper;
import com.dpline.dao.rto.DataSourceRto;
import com.dpline.datasource.api.client.DataSourceClient;
import com.dpline.datasource.api.common.CommonConnectionParam;
import com.dpline.datasource.api.utils.DataSourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataSourceServiceImpl extends GenericService<DataSource, Long> implements DataSourceService {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceServiceImpl.class);

    public DataSourceServiceImpl(@Autowired DataSourceMapper dataSourceMapper) {
        super(dataSourceMapper);
    }

    public DataSourceMapper getMapper() {
        return (DataSourceMapper) super.genericMapper;
    }

    @Override
    public int create(DataSource dataSource) {
        // 判断是否有同名
        Boolean flag = conflictDataSource(dataSource.getDataSourceName());
        if (flag) {
            return -1;
        }
        int insertFlag = insert(dataSource);
        return insertFlag;
    }

    @Override
    public Boolean checkConnect(DataSourceDto dataSourceDto) {
        if (dataSourceDto == null) {
            return false;
        }
        if (conflictDataSource(dataSourceDto.getDataSourceName())) {
            return false;
        }
        CommonConnectionParam commonConnectionParam = BeanUtil.copyProperties(dataSourceDto, CommonConnectionParam.class);
        DataSourceClient dataSourceClient = DataSourceUtil.getDataSourceClient(commonConnectionParam, DataSourceType.getType(dataSourceDto.getDataSourceType()));
        Boolean flag = dataSourceClient.checkClient();
        return flag;
    }

    @Override
    public Pagination<DataSource> list(DataSourceRto dataSourceRto) {
        Pagination<DataSource> dataSourcePagination = Pagination.getInstanceFromRto(dataSourceRto);
        this.executePagination(x -> this.getMapper().list(x), dataSourcePagination);
        return dataSourcePagination;
    }

    /**
     * 判断是否有同名等存在冲突数据源
     *
     * @param dataSourceName
     * @return
     */
    private Boolean conflictDataSource(String dataSourceName) {
        boolean flag = false;
        DataSource queryData = new DataSource();
        queryData.setDataSourceName(dataSourceName);
        List<DataSource> dataSourceList = selectAll(queryData);
        if (CollUtil.isNotEmpty(dataSourceList)) {
            flag = true;
        }
        return flag;
    }
}
