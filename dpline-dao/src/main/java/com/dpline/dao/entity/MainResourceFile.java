package com.dpline.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dpline.dao.generic.GenericModel;
import lombok.Data;

@TableName("dpline_main_resource")
@Data
public class MainResourceFile extends GenericModel<Long> {

    private String name;

    private String jarFunctionType;

    private String runMotorType;

    private String jarAuthType;

    private Long projectId;

    private String description;

}
