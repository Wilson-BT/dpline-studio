package com.handsome.common.enums;

public enum FlinkK8sExposedType {

    /**
     * LoadBalancer
     */
    LoadBalancer(0,"LoadBalancer"),

    /**
     * ClusterIP
     */
    ClusterIP(1,"ClusterIP"),

    /**
     * NodePort
     */
    NodePort(2,"NodePort");

    private final Integer key;

    private final String value;

    FlinkK8sExposedType(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public static FlinkK8sExposedType of(Integer value) {
        for (FlinkK8sExposedType order : values()) {
            if (order.value.equals(value)) {
                return order;
            }
        }
        return null;
    }

    public Integer getName() {
        return key;
    }

    public String getValue() {
        return value;
    }


}
