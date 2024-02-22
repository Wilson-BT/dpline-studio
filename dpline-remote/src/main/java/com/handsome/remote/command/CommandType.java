
package com.handsome.remote.command;

public enum CommandType {

    /**
     * remove task log response
     */
    REMOVE_TAK_LOG_RESPONSE,

    /**
     * roll view log request
     */
    ROLL_VIEW_LOG_REQUEST,

    /**
     * roll view log response
     */
    ROLL_VIEW_LOG_RESPONSE,

    /**
     * view whole log request
     */
    VIEW_WHOLE_LOG_REQUEST,

    /**
     * view whole log response
     */
    VIEW_WHOLE_LOG_RESPONSE,

    /**
     * get log bytes request
     */
    GET_LOG_BYTES_REQUEST,

    /**
     * get log bytes response
     */
    GET_LOG_BYTES_RESPONSE,


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

    /**
     * k8s client add and start
     */
    K8S_CLIENT_ADD_REQUEST,

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
    TASK_TRIGGER_RESPONSE;
}
