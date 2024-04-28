package com.dpline.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dpline.dao.generic.GenericModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("dpline_file")
public class File extends GenericModel<Long> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 字段名称：文件名称
     *
     * 数据库字段信息:file_name VARCHAR(255)
     */
    private String fileName;

    /**
     * 字段名称：文件类型
     *
     * 数据库字段信息:file_type TINYINT(3)
     */
    private String fileType;

    /**
     * 文件描述
     */
    private String description;

    /**
     * 字段名称：文件内容
     *
     * 数据库字段信息:content TEXT(65535)
     */
    private String content;
    /**
     * 字段名称：元表sql
     *
     * 数据库字段信息:meta_table_content TEXT(65535)
     */
    private String metaTableContent;
    /**
     * 字段名称：转换语句sql
     *
     * 数据库字段信息:etl_content TEXT(65535)
     */
    private String etlContent;

    /**
     * 字段名称：配置内容
     *
     * 数据库字段信息:config_content TEXT(65535)
     */
    private String configContent;

    /**
     * 字段名称：资源内容
     *
     * 数据库字段信息:source_content TEXT(65535)
     */
    private String sourceContent;

    /**
     * 字段名称：dataStream配置
     *
     * 数据库字段信息:data_stream_config TEXT(65535)
     */
    private String dataStreamContent;


    /**
     * 字段名称：文件状态
     *
     * 数据库字段信息:file_status TINYINT(3)
     */
    private String fileStatus;

    /**
     * 字段名称：项目ID
     *
     * 数据库字段信息:project_id BIGINT(19)
     */
    private Long projectId;

    /**
     * 字段名称：目录ID
     *
     * 数据库字段信息:folder_id BIGINT(19)
     */
    private Long folderId;

    /**
     * 运行
     */
    private String runMotorType;


    private String dag;


    public File() {
    }


    public File(int enabledFlag) {
        this.enabledFlag = enabledFlag;
    }


}