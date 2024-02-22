package com.handsome.console.websocket;

import io.undertow.util.CopyOnWriteMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Set;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * @author benjobs
 */
@Slf4j
@Component
@ServerEndpoint(value = "/websocket/{sid}")
public class WebSocketEndpoint {
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象
    private static CopyOnWriteMap<String, Session> socketSessions = new CopyOnWriteMap<>();

    @Getter
    private String sid;

    @Getter
    private Session session;

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        log.info("websocket onOpen...sid:{}",sid);
        this.sid = sid;
        this.session = session;
        socketSessions.put(sid,session);
    }

    @OnClose
    public void onClose() throws IOException {
        log.info("websocket onClose....,{}",this.sid);
        this.session.close();
        socketSessions.remove(this.sid);
    }

    @OnError
    public void onError(Session session, Throwable e) {
        e.printStackTrace();
    }

    public static void sendMessage(String socketId, String message) {
        try {
            Session session = socketSessions.get(socketId);
            if (session != null) {
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 用于给 所有客户端发消息
     *
     * @param object
     */
    public void sendMessageToAll(Object object){
        Set<String> sessionIds = socketSessions.keySet();
        for (String str : sessionIds) {
            System.out.println("websocket广播消息：" + object.toString());
            try {
                //服务器主动推送
                socketSessions.get(str).getBasicRemote().sendObject(object) ;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 用于给单个指定的客户端发消息
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message,Session session){
        log.info("server receivered {} from {}",message,this.sid);
    }
}

