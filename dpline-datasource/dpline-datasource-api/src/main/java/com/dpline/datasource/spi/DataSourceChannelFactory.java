package com.dpline.datasource.spi;

public interface DataSourceChannelFactory {

    /**
     * 获取名称
     *
     * @return
     */
    String getName();

    /**
     * 创建链接
     */
    DataSourceChannel create();
}
