package com.dpline.console.handler;

import com.dpline.common.enums.RunModeType;
import com.dpline.common.store.FsStore;
import com.dpline.console.service.impl.JarFileServiceImpl;
import com.dpline.dao.domain.AbstractDeployConfig;
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
    FsStore fsStore;

    @Autowired
    JarFileServiceImpl jarFileServiceImpl;

//    private static Map<RunModeType, DeployHandler> deployExecutorMap = new HashMap<>();

    private static Map<RunModeType, DeployHandler> deployHandlerMap = new HashMap<>();


//    public DeployHandler getDeployExecutor(FileType taskType, RunModeType runModeType){
//        if(deployExecutorMap.containsKey(runModeType)){
//            return deployExecutorMap.get(runModeType);
//        }
//        switch (runModeType){
//            case K8S_SESSION:
//                return new SessionDeployHandler();
//            case K8S_APPLICATION:
//            case YARN_APPLICATION:
//                return deployExecutorMap.computeIfAbsent(runModeType,
//                        (key) -> {
//                            return new ApplicationDeployHandler(fsStore, jarFileServiceImpl);
//                        });
//            case YARN_SESSION:
//
//            default:
//                return null;
//        }
//    }

    public DeployHandler getDeployHandler(RunModeType runModeType, AbstractDeployConfig deployConfig){
        if(deployHandlerMap.containsKey(runModeType)){
            return deployHandlerMap.get(runModeType);
        }
        switch (runModeType){
            case K8S_APPLICATION:
            case YARN_APPLICATION:
                return deployHandlerMap.computeIfAbsent(runModeType,
                        (key) -> {
                            return new ApplicationDeployHandler(fsStore,deployConfig);
                        });
            default:
                return null;
        }
    }

    public DeployConfigConverter getDeployConfigConverter(RunModeType runModeType){
        switch (runModeType){
            case K8S_APPLICATION:
            case YARN_APPLICATION:
                return new FlinkDeployConfigConverter();
            default:
                return null;
        }
    }

}
