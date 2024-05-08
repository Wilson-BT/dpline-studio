package com.dpline.console.handler;

import org.springframework.web.socket.WebSocketSession;

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
