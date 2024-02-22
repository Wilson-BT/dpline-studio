package com.dpline.common.enums;


public enum OperationsEnum {
    DEPLOY("deploy"),
    START("start"),
    STOP("stop"),
    TRIGGER("trigger"),
    CANCEL("cancel"),
    RESTART("restart"),
    EXPLAIN("explain");

    private String operateType;

    OperationsEnum(String operateType){
        this.operateType = operateType;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

}
