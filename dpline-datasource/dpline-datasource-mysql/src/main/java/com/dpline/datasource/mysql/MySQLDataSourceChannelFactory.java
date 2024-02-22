package com.dpline.datasource.mysql;

import com.dpline.datasource.spi.DataSourceChannel;
import com.dpline.datasource.spi.DataSourceChannelFactory;
import com.google.auto.service.AutoService;


@AutoService(DataSourceChannelFactory.class)
public class MySQLDataSourceChannelFactory implements DataSourceChannelFactory {

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public DataSourceChannel create() {
        return new MySQLDataSourceChannel();
    }
}
