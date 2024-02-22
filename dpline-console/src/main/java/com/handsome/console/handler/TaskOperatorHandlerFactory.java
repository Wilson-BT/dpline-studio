package com.handsome.console.handler;

import com.handsome.common.enums.RunModeType;
import com.handsome.common.enums.TaskType;
import org.springframework.stereotype.Component;

/**
 * use to
 */
@Component
public class TaskOperatorHandlerFactory {

    public DeployHandler getDeployHandler(TaskType taskType, RunModeType runModeType){
        if(runModeType.equals(RunModeType.K8S_APPLICATION)){
            switch (taskType) {
                case SQL:
                    return new SqlTaskDeployHandler();
                case CUSTOM_CODE:
                    return new CustomCodeTaskDeployHandler();
            }
        }
        return new SessionDeployHandler();
    }

}
