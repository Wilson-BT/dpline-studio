package com.dpline.dao.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dpline.dao.generic.GenericModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("dpline_cluster")
@EqualsAndHashCode(callSuper=true)
public class Cluster extends GenericModel<Long> implements Serializable {

    private Long id;
    /**
     * 名称
     */
    private String clusterName;

    /**
     * 环境类型，test:测试,prod:生产
     */
    private String envType;

    /**
     * 参数
     */
    private String clusterParams;

    /**
     * 类型
     */
    private String clusterType;


    /**
     * 项目引用数
     */
    @TableField(exist = false)
    private Integer referProjectCount = 0;

    /**
     * 关联用户引用数
     */
    @TableField(exist = false)
    private Integer referUserCount = 0;
}
