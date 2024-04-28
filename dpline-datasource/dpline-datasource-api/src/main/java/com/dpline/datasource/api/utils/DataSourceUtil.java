package com.dpline.datasource.api.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.dpline.common.enums.DataSourceType;
import com.dpline.common.util.JSONUtils;
import com.dpline.common.util.SpringContextUtils;
import com.dpline.datasource.api.client.DataSourceClient;
import com.dpline.datasource.api.common.CommonConnectionParam;
import com.dpline.datasource.spi.DataSourceChannel;
import com.dpline.datasource.spi.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DataSourceUtil {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceUtil.class);

    private static DataSourceManager dataSourceManager;

    static {
        dataSourceManager = (DataSourceManager) SpringContextUtils.getBean("dataSourceManager", DataSourceManager.class);
    }

    public static CommonConnectionParam getConnectParam(String paramJsonStr) {
        if (StrUtil.isBlank(paramJsonStr)) {
            return null;
        }
        CommonConnectionParam commonConnectionParam = JSONUtil.toBean(paramJsonStr, CommonConnectionParam.class);
        return commonConnectionParam;
    }

    public static DataSourceClient getDataSourceClient(CommonConnectionParam commonConnectionParam, DataSourceType dataSourceType) {
        if (commonConnectionParam == null || dataSourceType == null) {
            return null;
        }
        DataSourceChannel dataSourceChannel = dataSourceManager.getDataSourceChannel(dataSourceType);
        DataSourceClient dataSourceClient = dataSourceChannel.createDataSourceClient(commonConnectionParam, dataSourceType);
        return dataSourceClient;
    }
}
