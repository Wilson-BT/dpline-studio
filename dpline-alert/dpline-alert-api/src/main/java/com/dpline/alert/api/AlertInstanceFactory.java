package com.dpline.alert.api;

import com.dpline.common.enums.AlertType;
import org.slf4j.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AlertInstanceFactory {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(AlertInstanceFactory.class);

    private final ConcurrentHashMap<String, AlertToolInstance> alertInstanceMap = new ConcurrentHashMap<>();

    @EventListener
    public void create(ApplicationReadyEvent readyEvent){
        ServiceLoader.load(AlertToolInstance.class).forEach(inst->{
            String name = inst.name();
            alertInstanceMap.put(name,inst);
            log.info("Registering alert plugin: {}", name);
        });
    }

    public AlertToolInstance getInstance(AlertType alertType){
        return alertInstanceMap.get(alertType.name());
    }




}
