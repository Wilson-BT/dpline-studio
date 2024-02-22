package com.dpline.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dpline.dao.generic.GenericModel;
import lombok.Data;

@Data
@TableName("dpline_jar_file_dtl")
public class JarFile extends GenericModel<Long> {

    /**
     * jar 包名称
     */
    private String jarName;


    /**
     * jar包保存位置
     */
    private String jarPath;

    /**
     * 引擎版本
     */
    private Long motorVersionId;

    /**
     * jar hash值
     */
    private String fileMd5;


    private String description;

    /**
     * 隶属资源
     */
    private Long mainResourceId;

    @TableField(exist = false)
    private String jarFunctionType;

    @TableField(exist = false)
    private String runMotorType;

    @TableField(exist = false)
    private String jarAuthType;

    @TableField(exist = false)
    private Long projectId;

    @TableField(exist = false)
    private String motorRealVersion;

}
