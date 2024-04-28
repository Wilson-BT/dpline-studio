package com.dpline.dao.dto;

import com.dpline.dao.entity.JarFile;
import com.dpline.dao.rto.GenericRto;
import lombok.Data;

@Data
public class JarFileDto extends GenericRto<JarFile> {

    Long id;

    String jarName;

    String fileMd5;

    Long mainResourceId;

    Long motorVersionId;

    String runMotorType;

    String description;

    String jarFunctionType;

}
