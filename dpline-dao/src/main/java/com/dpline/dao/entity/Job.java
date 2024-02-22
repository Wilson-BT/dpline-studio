package com.dpline.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dpline.dao.generic.GenericModel;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

@TableName("dpline_job")
@Data
public class Job extends GenericModel<Long> {

    /**
     * 字段名称：作业名称
     *
     * 数据库字段信息:job_name VARCHAR(255)
     */
    private String jobName;

    /**
     * 字段名称：作业内容
     *
     * 数据库字段信息:job_content TEXT(65535)
     */
    private String jobContent;

    /**
     * 运行方式
     */
    private String runModeType;

    /**
     * file_type
     */
    private String fileType;

    /**
     * 集群Id
     */
    private Long clusterId;

    /**
     * image id
     */
    private Long imageId;

    /**
     * 运行参数配置
     */
    private String runtimeOptions;

    /**
     * 运行引擎版本 ID
     */
    private Long motorVersionId;

    /**
     * 主jar包id
     */
    private Long mainJarId;


    private Long mainResourceId;

    /**
     * 主类名
     */
    private String mainClass;

    /**
     * 参数
     */
    private String appArgs;

    /**
     * 其他运行参数
     */
    private String otherRuntimeConfig;

    /**
     * 字段名称：资源内容、资源 Jar包 内容
     *
     * 数据库字段信息:source_content TEXT(65535)
     */
    private String sourceContent;

    /**
     * 字段名称：项目ID
     *
     * 数据库字段信息:project_id BIGINT(19)
     */
    private Long projectId;

    /**
     * 字段名称：文件ID
     *
     * 数据库字段信息:file_id BIGINT(19)
     */
    private Long fileId;

    /**
     * 执行状态
     */
    private String execStatus;

    /**
     * 是否已经部署
     */
    private Integer deployed;

    /**
     * rest api
     */
    @TableField(exist = false)
    private String restUrl;


    /**
     * file tag id
     */
    private Long fileTagId;


    /**
     * 任务运行类型 Flink or spark
     * default flink
     */
    private String runMotorType;

    /**
     * 告警模式
     */
    private String alertMode;

    /**
     * 告警Id
     */
    private Long alertInstanceId;

    /**
     * 运行的jobId
     */
    private String runJobId;

    /**
     * 目录 ID
     */
    @TableField(exist = false)
    private Long folderId;

    /**
     * 目录位置
     */
    @TableField(exist = false)
    private List<Long> folderIds;


    /**
     * 查询参数
     */
    @TableField(exist = false)
    @JsonIgnore
    private List<String> searchParams;

    /**
     * grafana 参数
     */
    @TableField(exist = false)
    private String grafanaUrl;

    /**
     * 全路径
     */
    @TableField(exist = false)
    private String fullPath;

    /**
     * slot 数量
     */
    @TableField(exist = false)
    private String jobManagerMem;

    @TableField(exist = false)
    private String taskManagerMem;

    @TableField(exist = false)
    private String fileTagName;

    @TableField(exist = false)
    private Integer parallelism;

    @TableField(exist = false)
    private String alertType;

    @TableField(exist = false)
    private String clusterParams;

}
