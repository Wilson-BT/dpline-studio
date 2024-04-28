package com.dpline.dao.dto;

import com.dpline.dao.generic.GenericModel;
import lombok.Data;

import java.io.Serializable;

@Data
public class DplineFileDto extends GenericModel<Long> implements Serializable {

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
    private String dataStreamConfig;

    /**
     * 字段名称：文件状态
     *
     * 数据库字段信息:file_status TINYINT(3)
     */
    private String fileStatus;

    /**
     * 字段名称：锁定人
     *
     * 数据库字段信息:locked_by VARCHAR(255)
     */
    private String lockedUser;

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

    private String projectName;

//    private JobConfig jobConfig;
//    private RunResourceConfig sourceConfig;
    /**
     * 是否上线标识，1：上线，0，未上线
     */
    private Integer isOnLine;
    /**
     * 判断是否已上锁,
     */
    private Long lockSign;

    private String dag;

    private String version;

    private String businessFlag;

}
