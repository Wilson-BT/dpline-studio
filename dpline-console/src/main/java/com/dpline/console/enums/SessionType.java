package com.dpline.console.enums;

public enum SessionType {
    OPERATE_LOG, OTHER;

    public static SessionType of(String name){
        for (SessionType sessionType : SessionType.values()) {
            if(sessionType.name().equals(name)){
                return sessionType;
            }
        }
        return null;
    }
}
