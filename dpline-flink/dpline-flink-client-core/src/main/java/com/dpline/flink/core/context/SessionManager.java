package com.dpline.flink.core.context;

import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    /**
     * session Id
     */
    private static final ConcurrentHashMap<String, SessionStudio> sessionMap = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public SessionStudio openSession(AbstractSessionContext sessionContext){
        SessionId sessionId = SessionId.create();
        return sessionMap.computeIfAbsent(sessionId.getIdentifier().toString(),(key) -> {
            return SessionStudio
                .builder()
                .sessionId(sessionId)
                .sessionContext(sessionContext)
                .lastActiveTime(System.currentTimeMillis())
                .build();
            });
    }

    public void closeSession(String sessionId){
        sessionMap.remove(sessionId);
    }

    public SessionStudio getSession(String sessionId){
        return sessionMap.get(sessionId);
    }
    // 线程定时删除


}
