package com.dpline.dao.domain;

import com.dpline.common.enums.FileType;
import com.dpline.common.enums.RunModeType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FlinkDeployConfig extends AbstractDeployConfig {

    /**
     * 主jar包
     */
    JarDepend mainJar;

    /**
     * 连接器 配置
     */
    Map<Long, JarDepend> connectorIdPathMap = new HashMap<>();

    /**
     * 扩展包配置
     */
    Map<Long,JarDepend> extendIdPathMap = new HashMap<>();

    /**
     * 扩展包配置
     */
    Map<Long,JarDepend> udfPathMap = new HashMap<>();


    /**
     * Sql text
     */
    String sqlText;

    /**
     * 文件类型，sql or ds
     */
    FileType fileType;


}
