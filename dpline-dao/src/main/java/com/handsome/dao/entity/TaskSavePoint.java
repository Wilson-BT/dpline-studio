package com.handsome.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.handsome.common.enums.DoCheckpointType;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@TableName("dpline_task_savepoint")
public class TaskSavePoint {

    @TableId(value = "id")
    private long id;

    @TableField("task_id")
    private long taskId;

    @TableField("save_point_address")
    private String savePointAddress;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
