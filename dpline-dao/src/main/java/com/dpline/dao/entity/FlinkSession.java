package com.dpline.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.dpline.common.enums.Flag;
import lombok.Data;

import java.util.Date;

@Data
@TableName("dpline_flink_session")
public class FlinkSession {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "flink_session_name")
    private String flinkSessionName;

    @TableField(value = "k8s_namespace_id")
    private long k8sNamespaceId;

    @TableField(value = "taskmanager_num")
    private int taskmanagerNum;

    @TableField(value = "taskmanager_mem_size")
    private int taskmanagerMemSize;

    @TableField(value = "taskmanager_cpu_num")
    private int taskmanagerCpuNum;

    @TableField(value = "taskmanager_slot_num")
    private int taskmanagerSlotNum;

    @TableField(value = "jobmanager_process_size")
    private int jobmanagerProcessSize;

    @TableField(value = "kubernetes_cluster_id")
    private Long kubernetesClusterId;

    /**
     * is useful
     */
    @TableField(value = "status")
    private Flag status;

    @TableField(value = "user_id")
    private int useId;

//    @TableField(value = "restart_options")
//    private RestartOptions restartOptions;
//
//    @TableField(value = "checkpoint_options")
//    private CheckpointOptions checkpointOptions;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @TableField(exist = false)
    private String nameSpace;

    @TableField(exist = false)
    private String kubePath;

}
