package com.dpline.console.socket;


import com.dpline.console.handler.WebSocketHandler;
import io.undertow.util.CopyOnWriteMap;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WsSessionManager {
    /**
     * 保存连接 session 的地方
     */
    private static ConcurrentHashMap<Long, WebSocketSession> SESSION_POOL = new ConcurrentHashMap<>();

    /**
     * 添加 session
     * @param key
     */
    public static void add(Long key, WebSocketSession session) {
        // 添加 session
        SESSION_POOL.put(key, session);
    }

    public static void addExecutor(Long key, WebSocketSession session) {
        // 添加 session
        SESSION_POOL.put(key, session);
    }

    /**
     * 删除 session,会返回删除的 session
     * @param key
     * @return
     */
    public static WebSocketSession remove(Long key) {
        // 删除 session
        return SESSION_POOL.remove(key);
    }

    /**
     * 删除并同步关闭连接
     * @param key
     */
    public static void removeAndClose(Long key) {
        WebSocketSession session = remove(key);
        if (session != null) {
            try {
                // 关闭连接
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 获得 session
     * @param key
     * @return
     */
    public static WebSocketSession get(Long key) {
        // 获得 session
        return SESSION_POOL.get(key);
    }



}
