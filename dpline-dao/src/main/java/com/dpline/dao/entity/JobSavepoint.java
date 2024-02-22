package com.dpline.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dpline.dao.generic.GenericModel;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("dpline_job_savepoint")
public class JobSavepoint extends GenericModel<Long> implements Serializable {

    private Long jobId;

    private String savepointName;

    private String savepointPath;

}
