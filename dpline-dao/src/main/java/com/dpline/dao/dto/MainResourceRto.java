package com.dpline.dao.dto;

import com.dpline.dao.entity.MainResourceFile;
import com.dpline.dao.rto.GenericRto;
import lombok.Data;

@Data
public class MainResourceRto extends GenericRto<MainResourceFile> {

    Long mainResourceId;

    String name;

    String jarAuthType;

    Long projectId;

    String jarFunctionType;

    String description;

    String runMotorType;

}
