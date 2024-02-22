package com.handsome.dao.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.handsome.common.enums.DraftTagType;
import lombok.Data;

import java.util.Date;

@TableName("dpline_flink_tag_task_udf_relation")
@Data
public class FlinkTagTaskUdfRelation {
    /**
     *
     */
    @TableId(value = "id")
    private long id;

    /**
     * flink resource Id
     */
    @TableField(value = "udf_id")
    private int udfId;

    @TableField(value = "draft_tag_type")
    private DraftTagType draftTagType;

    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * update time
     */
    @TableField(value = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
