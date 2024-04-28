package com.dpline.common.enums;

public enum CompareSource {
    /**
     * 对比类型:file
     */
    FILE_COMPARE("file"),
    /**
     * 对比类型:job
     */
    JOB_COMPARE("job");

    private String name;

    CompareSource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public static CompareSource of (String name){
        CompareSource value1 = null;
        for (CompareSource value : values()) {
            if (value.getName().equals(name)){
                value1 = value;
            }
        }
        return value1;
    }
}
