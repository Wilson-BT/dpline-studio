package com.dpline.console.socket;

import com.dpline.console.handler.WebSocketHandler;
import io.undertow.util.CopyOnWriteMap;

import java.util.Map;

public class WsTaskManager {

    private static final Map<Long, WebSocketHandler> TASK_WEBSOCKET_MAP = new CopyOnWriteMap<>();

    /**
     * 添加 session
     * @param key
     */
    public static void add(Long key, WebSocketHandler session) {
        // 添加 session
        TASK_WEBSOCKET_MAP.put(key, session);
    }


    /**
     * 删除 session,会返回删除的 session
     * @param key
     * @return
     */
    public static WebSocketHandler remove(Long key) {
        // 删除 session
        return TASK_WEBSOCKET_MAP.remove(key);
    }

    /**
     * 删除并同步关闭连接
     * @param key
     */
    public static void removeAndClose(Long key) {
        WebSocketHandler webSocketHandler = remove(key);
        if (webSocketHandler != null) {
            // 关闭连接
            webSocketHandler.close();
        }
    }
    /**
     * 获得 session
     * @param key
     * @return
     */
    public static WebSocketHandler get(Long key) {
        // 获得 session
        return TASK_WEBSOCKET_MAP.get(key);
    }

}
