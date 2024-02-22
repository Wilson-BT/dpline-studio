package com.handsome.console.service.impl;

import com.handsome.console.service.WebSocketService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    /**
     * send message
     * @param socketId
     * @param taskInstanceId
     */
    @Override
    @Async("LogListenerExecutor")
    public void startListenLogAndSendWebsocket(String socketId, long taskInstanceId) {


    }
}
