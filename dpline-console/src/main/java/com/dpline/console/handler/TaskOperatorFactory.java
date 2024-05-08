package com.dpline.console.handler;

import com.dpline.common.enums.FileType;
import com.dpline.common.enums.RunModeType;
import com.dpline.common.store.Minio;
import com.dpline.console.service.impl.JarFileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Component
public class TaskOperatorFactory {

    @Autowired
    Minio minio;

    @Autowired
    JarFileServiceImpl jarFileServiceImpl;

    private static Map<RunModeType,DeployExecutor> deployExecutorMap = new HashMap<>();


    public DeployExecutor getDeployExecutor(FileType taskType, RunModeType runModeType){
        // 如果 map.get(runModeType)
        if(deployExecutorMap.containsKey(runModeType)){
            return deployExecutorMap.get(runModeType);
        }
        switch (runModeType){
            case K8S_APPLICATION:
                return deployExecutorMap.computeIfAbsent(runModeType,
                    (key) -> {
                        return new JobDeployExecutor(minio, jarFileServiceImpl);
                });
            case K8S_SESSION:
                return new SessionDeployHandler();
            default:
                return null;
        }
    }

}
