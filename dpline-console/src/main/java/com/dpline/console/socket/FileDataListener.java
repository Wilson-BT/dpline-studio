package com.dpline.console.socket;

import lombok.SneakyThrows;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;



public class FileDataListener extends TailerListenerAdapter {

    public static final String DEPLOY_FAILED_FLAG = "DEPLOY FAILED";
    public static final String DEPLOY_SUCCESS_FLAG = "DEPLOY SUCCESS";
    public static final String EOF_FLAG = "READ EOF";
    public volatile boolean readEnd = false;

    private static Logger logger = LoggerFactory.getLogger(FileDataListener.class);

    private WebSocketSession webSocketSession;

    public FileDataListener(WebSocketSession webSocketSession) {
        this.webSocketSession=webSocketSession;
    }

    @Override
    public void fileNotFound() {  //文件没有找到
        logger.error("文件没有找到");
        try {
            webSocketSession.sendMessage(new TextMessage("文件没有找到..."));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fileRotated() {  //文件被外部的输入流改变
        logger.error("文件rotated");
        try {
            webSocketSession.sendMessage(new TextMessage("文件被修改..."));
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.fileRotated();
    }

    @Override
    public void handle(String line) { //增加的文件的内容
        if(readEnd){
            logger.info("Log file read over,ready to exist...");
            return;
        }
        try {
            webSocketSession.sendMessage(new TextMessage(line));
            if (line.contains(DEPLOY_FAILED_FLAG) || line.contains(DEPLOY_SUCCESS_FLAG) || line.contains(EOF_FLAG)) {
                webSocketSession.sendMessage(new TextMessage(EOF_FLAG));
                logger.info("Log file read over,ready to exist...");
                readEnd = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public void handle(Exception ex) {
        throw new RuntimeException(ex);
//        super.handle(ex);
    }

}
