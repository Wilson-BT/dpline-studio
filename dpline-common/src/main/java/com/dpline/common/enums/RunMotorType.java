package com.dpline.common.enums;

import java.util.Optional;

public enum RunMotorType {

    FLINK(0,"FLINK","org.apache.flink"),

    SPARK(1,"SPARK","org.apache.spark");

    private int key;

    private String value;

    private String classPrefix;

    RunMotorType(int key, String value,String classPrefix) {
        this.key = key;
        this.value = value;
        this.classPrefix = classPrefix;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getClassPrefix() {
        return classPrefix;
    }

    public static Optional<RunMotorType> of(String runMotor){
        for (RunMotorType value : RunMotorType.values()){
            if(value.getValue().equals(runMotor)){
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }


}
