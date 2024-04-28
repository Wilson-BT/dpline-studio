package com.dpline.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dpline.dao.generic.GenericModel;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("dpline_job_operate_log")
public class DplineJobOperateLog extends GenericModel<Long> implements Serializable {

    private Long jobId;

    private String operateType;

    private Long operateTimestamp;

    private String operateLogPath;

    private String traceId;

}
