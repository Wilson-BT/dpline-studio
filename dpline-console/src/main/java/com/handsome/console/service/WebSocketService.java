package com.handsome.console.service;

public interface WebSocketService {

    void startListenLogAndSendWebsocket(String socketId, long taskInstanceId);
}