package com.dpline.console.socket;

import cn.hutool.cron.task.Task;
import com.dpline.common.util.Asserts;
import com.dpline.console.enums.SessionType;
import com.dpline.console.handler.LogSocketHandler;
import com.dpline.console.handler.WebSocketHandler;
import io.undertow.util.CopyOnWriteMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 直接读取 LogId 对应的地址文件
 */
@Component
public class WebSocketEndpointHandler extends TextWebSocketHandler {

    private final static Logger logger = LoggerFactory.getLogger(WebSocketEndpointHandler.class);

    private static Pattern URL_PATH_PATTERN = Pattern.compile("/websocket/([a-zA-Z]+)/(\\d+)");

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("websocket onOpen....");
        String path = session.getUri().getPath();
        Matcher matcher = URL_PATH_PATTERN.matcher(path);
        if(!matcher.matches()){
            return;
        }
        String sessionType = matcher.group(1);
        Long id = Long.getLong(matcher.group(2));
        WsSessionManager.add(id, session);
        SessionType ofSessionType = SessionType.of(sessionType);
        if(Asserts.isNull(ofSessionType) || !SessionType.OPERATE_LOG.equals(SessionType.of(sessionType))){
            return;
        }
        WebSocketHandler webSocketHandler = new LogSocketHandler(id);
        WsTaskManager.add(id, webSocketHandler);
        webSocketHandler.trigger();
    }


    /**
     * close session
     * @param session
     * @param status
     * @throws Exception
     */
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("websocket onClose....");
        if (session.isOpen()) {
            session.close();
        }
        Matcher matcher = URL_PATH_PATTERN.matcher(session.getUri().getPath());
        if(!matcher.matches()){
            return;
        }
        removeClose(Long.getLong(matcher.group(2)));
        logger.info("webSocketHandler onClose....");
    }

    public void removeClose(Long id){
        WsSessionManager.removeAndClose(id);
        WsTaskManager.removeAndClose(id);
    }

}
