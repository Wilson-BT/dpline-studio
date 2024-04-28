package com.dpline.datasource.mysql;


import com.dpline.common.enums.DataSourceType;
import com.dpline.datasource.api.client.CommonDataSourceClient;
import com.dpline.datasource.api.common.CommonConnectionParam;

public class MySQLDataSourceClient extends CommonDataSourceClient {

    public MySQLDataSourceClient(CommonConnectionParam commonConnectionParam, DataSourceType dataSourceType) {
        super(commonConnectionParam, dataSourceType);
    }
}
