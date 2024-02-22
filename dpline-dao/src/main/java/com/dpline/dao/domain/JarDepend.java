package com.dpline.dao.domain;

import com.dpline.common.enums.JarType;
import com.dpline.dao.entity.JarFile;
import lombok.Data;

import java.io.Serializable;

@Data
public class JarDepend implements Serializable {

    Long jarId;

    String jarName;

    String jarFunctionType;

    String jarPath;

    Long mainResourceId;

    public JarDepend (JarFile jarFile){
        this.jarId = jarFile.getId();
        this.jarName = jarFile.getJarName();
        this.jarFunctionType = JarType.FunctionType.MAIN.name();
        this.jarPath = jarFile.getJarPath();
        this.mainResourceId = jarFile.getMainResourceId();
    }

    public JarDepend() {
    }
}
