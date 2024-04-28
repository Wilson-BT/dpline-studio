package com.dpline.datasource.api.client;

import java.sql.Connection;

public interface DataSourceClient extends AutoCloseable {

    /**
     * 获取名称
     */
    String getName();

    Boolean checkClient();

    @Override
    void close();

    Connection getConnection();
}
