package com.dpline.flink.core;

import com.dpline.common.enums.OperationsEnum;
import com.dpline.flink.core.funtion.TaskStopFunction;
import com.dpline.flink.core.funtion.TaskExplainFunction;
import com.dpline.flink.core.funtion.TaskSubmitFunction;
import com.dpline.flink.core.funtion.TaskTriggerFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FlinkTaskOperateFactory {

    private static FlinkTaskOperateFactory flinkTaskOperateFactory = new FlinkTaskOperateFactory();

    private static Map<OperationsEnum,TaskOperator> taskOperatorMap = new HashMap<OperationsEnum, TaskOperator>();

    private FlinkTaskOperateFactory(){
    }

    public static FlinkTaskOperateFactory getInstance(){
        return flinkTaskOperateFactory;
    }

    public synchronized TaskOperator getOperator(OperationsEnum operationsEnum){

        return taskOperatorMap.computeIfAbsent(operationsEnum, new Function<OperationsEnum, TaskOperator>() {

            @Override
            public TaskOperator apply(OperationsEnum operationsEnum) {

                switch (operationsEnum){
                    case STOP:
                        return new TaskStopFunction();
                    case START:
                        return new TaskSubmitFunction();
                    case EXPLAIN:
                        return new TaskExplainFunction();
                    case TRIGGER:
                        return new TaskTriggerFunction();
                    default:
                        throw new RuntimeException("Not support operate type.");
                }
            }
        });
    }
}
