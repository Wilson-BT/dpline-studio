package com.dpline.flink.core.funtion;

import com.dpline.common.enums.ResponseStatus;
import com.dpline.common.request.*;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.ExceptionUtil;
import com.dpline.flink.core.TaskOperator;

import java.lang.reflect.Method;

public class TaskExplainFunction extends TaskOperator {

    @Override
    public Response apply(FlinkRequest flinkRequest) {
        FlinkDagResponse flinkDagResponse = new FlinkDagResponse("", ResponseStatus.FAIL, "");
        FlinkDagRequest flinkDagRequest = (FlinkDagRequest)flinkRequest;
        if(Asserts.isNull(flinkRequest)){
            return null;
        }
        // 加载 submit 类
        String className = "";
        switch(flinkDagRequest.getFileType()){
            case DATA_STREAM:
                className = "com.dpline.flink.explain.FlinkJarInterpreter";
                break;
            case SQL_STREAM:
                // 如果是sql模式，需要根据sql 的类型，进行区分，如果
                className = "com.dpline.flink.explain.FlinkSqlInterpreter";
                break;
        }
        try {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            Method explainMethod = clazz.getDeclaredMethod("explain", FlinkRequest.class);
            explainMethod.setAccessible(true);
            flinkDagResponse = (FlinkDagResponse) explainMethod.invoke(clazz.newInstance(), flinkDagRequest);
        } catch (Exception ex) {
            flinkDagResponse.setMsg(ExceptionUtil.exceptionToString(ex));
        }
        return flinkDagResponse;
    }

}
