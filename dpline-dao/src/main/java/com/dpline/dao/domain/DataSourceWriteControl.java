package com.dpline.dao.domain;

import lombok.Data;

/**
 * 数据源写入控制
 */
@Data
public class DataSourceWriteControl {
    /**
     * 数据源类型
     */
    private String dataSourceType;

    /**
     * 需要控制的内容
     */
    private String content;

}
