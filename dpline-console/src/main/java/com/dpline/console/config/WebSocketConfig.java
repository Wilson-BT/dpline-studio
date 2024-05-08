package com.dpline.console.config;

import com.dpline.console.handler.WebSocketHandler;
import com.dpline.console.socket.WebSocketEndpointHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/websocket/**")
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketEndpointHandler myHandler() {
        return new WebSocketEndpointHandler();
    }

}

