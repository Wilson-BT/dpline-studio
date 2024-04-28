
package com.dpline.remote.command;

public enum CommandType {

    /**
     * execute task request
     */
    TASK_RUN_REQUEST,

    /**
     * execute task ack
     */
    TASK_RUN_ACK,

    /**
     * execute task response
     */
    TASK_RUN_RESPONSE,

    /**
     * task run request7
     */
    TASK_STOP_REQUEST,

    /**
     * kill task response
     */
    TASK_STOP_RESPONSE,

    /**
     * task add in watcher
     */
    TASK_WATCH_REQUEST,

    /**
     * task rm out from watcher
     */
    TASK_REMOVE_WATCH_REQUEST,

    /**
     * heart beat
     */
    HEART_BEAT,

    /**
     * test connect ping
     */
    PING,

    /**
     * test connect pong
     */
    PONG,

    /**
     * k8s client stop and remove
     */
    K8S_CLIENT_REMOVE_REQUEST,

    K8S_CLIENT_REMOVE_RESPONSE,

    /**
     * k8s client add and start
     */
    K8S_CLIENT_ADD_REQUEST,

    K8S_CLIENT_ADD_RESPONSE,

    /**
     * k8s client 更新
     */
    K8S_CLIENT_UPDATE_REQUEST,

    K8S_CLIENT_UPDATE_RESPONSE,

    /**
     * delete ingress
     */
    INGRESS_DELETE_REQUEST,

    /**
     * add ingress
     */
    INGRESS_ADD_REQUEST,

    /**
     * task trigger request
     */
    TASK_TRIGGER_REQUEST,

    /**
     * task trigger response
     */
    TASK_TRIGGER_RESPONSE,

    /**
     * task alert edit request
     */
    TASK_ALERT_EDIT_REQUEST,

    /**
     * task alert edit response
     */
    TASK_ALERT_EDIT_RESPONSE,

    /**
     * request file request
     */
    FILE_DAG_REQUEST,

    /**
     * request file response
     */
    FILE_DAG_RESPONSE,

    TEST_REQ,

    TEST_RESP;
}
