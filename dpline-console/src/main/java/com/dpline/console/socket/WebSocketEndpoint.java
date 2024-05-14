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
//import java.util.Optional;
//import java.util.concurrent.ConcurrentHashMap;
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
//    private static final Map<Long, WebSocketHandler> WEB_SOCKET_HANDLER_MAP = new ConcurrentHashMap<>();
//
//    private WebSocketEndpoint(){
//        logger.info("我是WebSocketEndpoint");
//    }
//
//    @OnOpen
//    public void onOpen(Session session, @PathParam("sessionType") String sessionType, @PathParam("id") Long id) {
//        logger.info("websocket onOpen....");
//        WsSessionManager.add(id, session);
//        SessionType ofSessionType = SessionType.of(sessionType);
//        if(Asserts.isNull(ofSessionType)){
//            return;
//        }
//        WebSocketHandler webSocketHandler = WEB_SOCKET_HANDLER_MAP.computeIfPresent(id, (k, v) -> {
//            return new LogSocketHandler();
//        });
//        if(Asserts.isNull(webSocketHandler)){
//            return;
//        }
//        webSocketHandler.init(id);
//        webSocketHandler.trigger();
//    }
//
//    @OnClose
//    public void onClose(Session session, @PathParam("id") Long id) throws IOException {
//        logger.info("websocket onClose....");
//        Optional.ofNullable(WsSessionManager.get(id)).ifPresent(
//                s -> {
//                    try {
//                        s.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//        );
//        WsSessionManager.remove(id);
//        Optional.ofNullable(WEB_SOCKET_HANDLER_MAP.get(id)).ifPresent(WebSocketHandler::close);
//        WEB_SOCKET_HANDLER_MAP.remove(id);
//        logger.info("webSocketHandler onClose....");
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
////    public void sendMessage(String message) throws IOException {
////        sendMessage(message,WsSessionManager.get(this.id));
////    }
////
////    /**
////     * 服务端发送消息给客户端
////     */
////    public void sendMessage(String message, Session toSession) throws IOException {
////        if(Asserts.isNull(toSession)){
////            return;
////        }
//////        logger.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
////        toSession.getBasicRemote().sendText(message);
////    }
//
////
////    public enum WebSocketType {
////
////        DEPLOY,RUNNING,NOTICE;
////
////        public static WebSocketType of(String key){
////            return Arrays.stream(WebSocketType.values())
////                    .filter(val->val.name().equals(key))
////                    .findFirst()
////                    .orElse(null);
////        }
////    }
//
//}
