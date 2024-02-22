package com.handsome.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.handsome.common.enums.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * flink task instance to run
 */
@Data
@TableName("dpline_flink_run_task_instance")
public class FlinkRunTaskInstance implements Serializable {

    @TableId(value = "id")
    private long id;

    /**
     * flink task tag id
     */
    @TableField(value = "flink_task_tag_id")
    private long flinkTaskTagId;

    /**
     * 任务 id，使用 jobId，默认使用 task id 的值
     * clusterId,默认使用 task ID 的 hash 值，然后保存到数据库，必须要保证唯一
     */
    @TableField(value = "job_id")
    private String jobId;


    @TableField(value = "project_code")
    private long projectCode;

    /**
     * 任务描述
     */
    @TableField(value = "description")
    private String description;
    /**
     * flink task tag name
     */
    @TableField("flink_task_instance_name")
    private String flinkTaskInstanceName;

    /**
     * run mode
     */
    @TableField("run_mode")
    private RunModeType runMode;

    /**
     * if open chain
     */
    @TableField("open_chain")
    private Flag openChain;

    /**
     * memory、cpu、slots、parallelism and so on
     * json
     */
    @TableField("resource_options")
    private String resourceOptions;

    /**
     * resolve order
     * default: parent first
     */
    @TableField("resolve_order")
    private ResolveOrderType resolveOrder;

    /**
     * restart num
     * default:3 times
     */
    @TableField("restart_num")
    private int restartNum;

    /**
     * whether do checkpoint
     */
    @TableField(exist = false)
    private CheckpointStartType checkpointType;

    /**
     * checkpoint address on s3
     */
    @TableField("checkpoint_address")
    private String checkpointAddress;

    /**
     * checkpoint json options
     */
    @TableField("checkpoint_options")
    private String checkpointOptions;

    /**
     * config address on minio
     */
    @TableField(value = "deploy_address")
    private String deployAddress;

    /**
     * exec status
     */
    @TableField(value = "exec_status")
    private ExecStatus execStatus;

    /**
     * is deployed on s3
     */
    @TableField(value = "deployed")
    private Flag deployed;

    /**
     * k8s namespace id
     */
    @TableField(value = "k8s_namespace_Id")
    private long K8sNamespaceId;

    /**
     * k8s cluster id
     * only used for k8s session mode
     */
    @TableField(value = "k8s_session_cluster_id")
    private long K8sSessionClusterId;

    /**
     * k8s pod port use nodeport or clusterId
     * default: nodeport
     * recommend: clusterIp
     */
    @TableField(value = "exposed_type")
    private ExposedType exposedType;

    /**
     * image id
     */
    @TableField(value = "flink_image_id")
    private int flinkImageId;

    /**
     * warning type
     */
    @TableField(value = "alert_type")
    private AlertType alertType;
    /**
     * warning group
     */
    @TableField(value = "alert_instance_id")
    private int alertInstanceId;

    @TableField(value = "restart_options")
    private String restartOptions;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @TableField(value = "user_id")
    private int userId;

    @TableField(value = "rest_url",strategy = FieldStrategy.NOT_EMPTY)
    private String restUrl;

    /**
     * k8s nameSpace name
     */
    @TableField(exist=false)
    private String nameSpace;

    /**
     * kubeConfigPath
     */
    @TableField(exist=false)
    private String kubePath;

    /**
     * flink Session Name used to create flink session
     */
    @TableField(exist = false)
    private String flinkSessionName;

    @TableField(exist = false)
    private String serviceAccount;

    @TableField(exist = false)
    private String imageFullName;

    @TableField(exist = false)
    private String selectorLables;
}
