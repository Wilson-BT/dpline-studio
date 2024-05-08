//package com.dpline.console.socket;
//import com.dpline.common.util.Asserts;
//import com.dpline.console.enums.SessionType;
//import com.dpline.console.handler.LogSocketHandler;
//import com.dpline.console.handler.WebSocketHandler;
//import io.undertow.util.CopyOnWriteMap;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//
//import javax.websocket.*;
//import javax.websocket.server.PathParam;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 直接读取 LogId 对应的地址文件
// */
//@Component
//@DependsOn("dplineJobOperateLogImpl")
//@ServerEndpoint(value = "/websocket/{sessionType}/{id}")
//public class WebSocketEndpoint {
//
//    private final static Logger logger = LoggerFactory.getLogger(WebSocketEndpoint.class);
//
//    private Long id;
//
//    private Session session;
//
//    private WebSocketHandler webSocketHandler;
//
//    private static final Map<Long, Session> SOCKET_SESSIONS = new CopyOnWriteMap<>();
//
//    private static final Map<SessionType, WebSocketHandler> WEB_SOCKET_HANDLER_MAP = new HashMap<>();
//
//    private WebSocketEndpoint(){
//        WEB_SOCKET_HANDLER_MAP.put(SessionType.OPERATE_LOG, new LogSocketHandler());
//    }
//
//    @OnOpen
//    public void onOpen(Session session, @PathParam("sessionType") String sessionType, @PathParam("id") Long id) {
//        logger.info("websocket onOpen....");
//        this.id = id;
//        this.session = session;
//        SOCKET_SESSIONS.put(id, session);
//        SessionType ofSessionType = SessionType.of(sessionType);
//        if(Asserts.isNull(ofSessionType)){
//            return;
//        }
//        webSocketHandler = WEB_SOCKET_HANDLER_MAP.get(SessionType.of(sessionType));
//        if(Asserts.isNull(webSocketHandler)){
//            return;
//        }
//        if(ofSessionType.equals(SessionType.OPERATE_LOG)){
//            webSocketHandler.init(id);
//        }
////        webSocketHandler.trigger(this);
//    }
//
//    @OnClose
//    public void onClose(Session session, @PathParam("id") Long id) throws IOException {
//        logger.info("websocket onClose....");
//        this.session.close();
//        webSocketHandler.close();
//        logger.info("webSocketHandler onClose....");
//        SOCKET_SESSIONS.remove(this.id);
//
//    }
//
//    @OnError
//    public void onError(Session session, Throwable e) {
//        logger.error(e.getMessage(), e);
//    }
//
//
//    /**
//     * 服务端发送消息给客户端
//     */
//    public void sendMessage(String message) throws IOException {
//        sendMessage(message,SOCKET_SESSIONS.get(this.id));
//    }
//
//    /**
//     * 服务端发送消息给客户端
//     */
//    public void sendMessage(String message, Session toSession) throws IOException {
//        if(Asserts.isNull(toSession)){
//            return;
//        }
////        logger.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
//        toSession.getBasicRemote().sendText(message);
//    }
//
//
//    public enum WebSocketType {
//
//        DEPLOY,RUNNING,NOTICE;
//
//        public static WebSocketType of(String key){
//            return Arrays.stream(WebSocketType.values())
//                .filter(val->val.name().equals(key))
//                .findFirst()
//                .orElse(null);
//        }
//    }
//
//}
