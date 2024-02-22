package com.dpline.dao.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dpline.dao.generic.GenericModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@TableName("dpline_data_source")
@EqualsAndHashCode(callSuper = true)
public class DataSource extends GenericModel<Long> implements Serializable {


    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    private String dataSourceName;


    /**
     * 数据源类型,例如:mysql
     */
    private String dataSourceType;

    /**
     * 数据源分类,例如:public:通用,project:项目私有
     */
    private String dataSourceFlag;

    /**
     * 项目私有时对应的项目编号
     */
    private Long projectId;

    /**
     * 环境类型，test:测试,prod:生产
     */
    private String env;

    /**
     * 数据源配置信息
     */
    private String connectionParams;

    /**
     * 描述信息
     */
    private String description;
}
