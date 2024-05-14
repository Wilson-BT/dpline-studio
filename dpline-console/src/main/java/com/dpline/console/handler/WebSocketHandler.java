package com.dpline.console.handler;


public interface WebSocketHandler {

    /**
     * 触发行为
     */
    void trigger();

    /**
     * 关闭行为
     * @param
     */
    void close();

}
