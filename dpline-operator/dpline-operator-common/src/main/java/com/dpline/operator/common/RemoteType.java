package com.dpline.operator.common;

import lombok.Getter;

import java.util.Optional;

public enum RemoteType {
    REST(0, "rest"),
    YARN(0, "yarn"),
    K8S(1, "k8s");
    @Getter
    private int key;

    @Getter
    private String type;

    RemoteType(int key, String type) {
        this.key = key;
        this.type = type;
    }

    public Optional<RemoteType> of(String type) {
        for (RemoteType remoteType : RemoteType.values()) {
            if (remoteType.type.equals(type)) {
                return Optional.of(remoteType);
            }
        }
        return Optional.empty();
    }

}