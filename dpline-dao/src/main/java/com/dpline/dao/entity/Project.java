package com.dpline.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dpline.dao.generic.GenericModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;


@Data
@TableName("dpline_project")
@EqualsAndHashCode(callSuper=true)
public class Project extends GenericModel<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字段名称：项目名称
     *
     * 数据库字段信息:project_name VARCHAR(255)
     */
    private String projectName;


    /**
     * 项目描述
     */
    private String description;

}
