package com.dpline.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.Arrays;

/**
 *
 * 文件类型，分为普通文件和jar包文件，如果需要其他的，可能还会有所补充
 *
 */
public enum ResourceType {

    FILE(0, "normal textFile"),

    UDF(1,"udf to register");

    @EnumValue
    private Integer key;

    private String desc;

    ResourceType(Integer key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public Integer getKey() {
        return key;
    }

    public String getDesc() {
        return desc;
    }

    public static ResourceType of(int key) {
        return Arrays.stream(ResourceType.values())
                .filter((en) -> en.getKey() == key)
                .findFirst()
                .orElse(null);
    }

}
