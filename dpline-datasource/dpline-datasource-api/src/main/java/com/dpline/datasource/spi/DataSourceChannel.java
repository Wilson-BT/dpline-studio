package com.dpline.datasource.spi;

import com.dpline.common.enums.DataSourceType;
import com.dpline.datasource.api.client.DataSourceClient;
import com.dpline.datasource.api.common.CommonConnectionParam;

public interface DataSourceChannel {

    DataSourceClient createDataSourceClient(CommonConnectionParam commonConnectionParam, DataSourceType dataSourceType);
}
