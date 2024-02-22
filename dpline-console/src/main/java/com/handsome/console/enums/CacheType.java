package com.handsome.console.enums;

public enum CacheType {
    USER("user"),
    TASK_DEFINITION("taskDefinition");

    CacheType(String cacheName) {
        this.cacheName = cacheName;
    }

    private final String cacheName;

    public String getCacheName() {
        return cacheName;
    }
}
