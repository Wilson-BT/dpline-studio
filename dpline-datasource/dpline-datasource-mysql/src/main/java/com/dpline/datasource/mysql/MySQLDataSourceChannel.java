package com.dpline.datasource.mysql;


import com.dpline.common.enums.DataSourceType;
import com.dpline.datasource.api.common.CommonConnectionParam;
import com.dpline.datasource.spi.DataSourceChannel;
import com.dpline.datasource.api.client.DataSourceClient;

public class MySQLDataSourceChannel implements DataSourceChannel {

    @Override
    public DataSourceClient createDataSourceClient(CommonConnectionParam baseConnectionParam, DataSourceType dataSourceType) {
        return new MySQLDataSourceClient(baseConnectionParam, dataSourceType);
    }
}
