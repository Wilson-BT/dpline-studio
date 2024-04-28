package com.dpline.common.enums;

//import com.baomidou.mybatisplus.annotation.EnumValue;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum ExposedType  {

    NODE_PORT(0,"NodePort"),
    REBALANCE_PORT(1,"LoadBalancer"),
    CLUSTER_IP(2,"ClusterIP");

    @EnumValue
    int key;
    String value;

    ExposedType(int key,String value){
        this.key=key;
        this.value=value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
