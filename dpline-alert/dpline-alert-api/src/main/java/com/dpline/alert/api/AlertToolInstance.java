package com.dpline.alert.api;

public abstract class AlertToolInstance {

    public abstract String name();

    public abstract AlertResult send(AlertInfo alertInfo);
}
