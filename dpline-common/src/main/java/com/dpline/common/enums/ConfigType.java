package com.dpline.common.enums;

import java.util.Optional;

public enum ConfigType {
    SQL_CONFIG,
    DS_CONFIG,
    FLINK_CONFIG,
    JAR_CONFIG,
    JOB_CONFIG,
    SOURCE_CONFIG;

    public static Optional<ConfigType> of(String name){
        for (ConfigType configType : ConfigType.values()) {
            if(configType.name().equals(name)){
                return Optional.of(configType);
            }
        }
        return Optional.empty();
    }

}
