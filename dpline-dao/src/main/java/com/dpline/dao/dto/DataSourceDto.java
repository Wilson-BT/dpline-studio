package com.dpline.dao.dto;

import lombok.Data;

@Data
public class DataSourceDto {

    /**
     * 数据源名称
     */
    private String dataSourceName;

    /**
     * 数据源类型
     */
    private String dataSourceType;


    /**
     * 用户名
     */
    private String userName;


    /**
     * 密码
     */
    private String password;

    /**
     * 连接信息
     */
    protected String address;


    /**
     * jdbc连接信息
     */
    protected String dataSourceUrl;
}
