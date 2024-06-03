package com.dpline.flink.api;

import com.dpline.common.enums.OperationsEnum;
import com.dpline.common.request.FlinkRequest;
import com.dpline.common.request.Response;
import com.dpline.flink.core.ChildFirstClassLoader;
import com.dpline.flink.core.FlinkTaskOperateFactory;
import com.dpline.flink.core.TaskOperator;
import com.dpline.flink.core.TaskRunDynamicWrapper;
import com.dpline.flink.core.util.FlinkVersionClassLoader;

import java.util.concurrent.Semaphore;

/**
 * 代理接口
 */
public class TaskOperateProxy {

    private static final Semaphore semaphore = new Semaphore(15);

    public static Response execute(OperationsEnum operationsEnum,
                            FlinkRequest flinkRequest){

        Response response = null;
        try {
            semaphore.acquire();
            response = new TaskRunDynamicWrapper(
                FlinkTaskOperateFactory.getInstance().getOperator(operationsEnum)
            ).execute(flinkRequest);
//            response = new TaskRunDynamicWrapper(
//                    FlinkTaskOperateFactory.getInstance().getOperator(operationsEnum)
//            ).executeTest(flinkRequest);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        } finally {
            semaphore.release();
        }
        return response;
    }

//
//    /**
//     * @param classLoader
//     * @param operationsEnum
//     * @param flinkRequest
//     * @return
//     */
//    public static Response execute(
//                            ClassLoader classLoader,
//                            OperationsEnum operationsEnum,
//                            FlinkRequest flinkRequest){
//
//        Response response = null;
//        try {
//            semaphore.acquire();
//            ChildFirstClassLoader targetClassLoader = flinkVersionClassLoadUtil.getFlinkClientClassLoader(flinkRequest);
//            Thread.currentThread().setContextClassLoader(targetClassLoader);
//            TaskOperator operator = FlinkTaskOperateFactory.getInstance()
//                .getOperator(operationsEnum);
//            response = operator.apply(flinkRequest);
//        } catch (Exception exception) {
//            throw new RuntimeException(exception);
//        } finally {
//            Thread.currentThread().setContextClassLoader(classLoader);
//            semaphore.release();
//        }
//        return response;
//    }

}
