package com.dpline.common.enums;

import java.util.HashMap;

public enum JarAction {
    ADD("新增JAR"),
    RELOAD("上传新版本"),
    DELETE("删除JAR包"),
    UPDATE("编辑历史JAR");

    private String type;

    JarAction(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public static HashMap<String,String> getJobActionMap(){
        HashMap<String, String> map = new HashMap<>();
        for (JarAction value : JarAction.values()) {
            map.put(value.name(),value.type);
        }
        return map;
    }
}
