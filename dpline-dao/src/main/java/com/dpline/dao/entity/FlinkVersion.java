package com.dpline.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dpline.dao.generic.GenericModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@TableName("dpline_flink_version")
@EqualsAndHashCode(callSuper = true)
public class FlinkVersion extends GenericModel<Long> implements Serializable {

    /**
     * 名称
     */
    private String flinkName;

    /**
     * flink路径
     */
    private String flinkPath;

    /**
     * 版本
     */
    private String realVersion;

    /**
     * 描述信息
     */
    private String description;
}
