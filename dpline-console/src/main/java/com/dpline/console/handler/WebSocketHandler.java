package com.dpline.console.handler;

import com.dpline.console.socket.WebSocketEndpoint;

public interface WebSocketHandler {

    void init(Long id);

    /**
     * 触发行为
     * @param  webSocketEndpoint
     */
    void trigger(WebSocketEndpoint webSocketEndpoint);

    /**
     * 关闭行为
     * @param
     */
    void close();

}
