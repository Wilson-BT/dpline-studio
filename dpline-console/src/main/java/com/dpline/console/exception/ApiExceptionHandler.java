package com.dpline.console.exception;


import com.dpline.common.enums.Status;
import com.dpline.common.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

/**
 * @RestControllerAdvice 注解 应用在Controller上，属于加强Controller，会应用到所有的Controller上
 * @RestControllerAdvice 注释的类，其中的方法以及方法上的注解，即：
 * @ExceptionHandler会应用到所有Controller上；
 */
@RestControllerAdvice
@ResponseBody
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /**
     * 对于方法
     * @param e controller 类的方法所raise的异常
     * @param hm 被注解的方法
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e, HandlerMethod hm) {
        ApiException ce = hm.getMethodAnnotation(ApiException.class);
        // 判断是否被 @ApiException 注解，如果没有，将发出服务异常报错,并将异常信息抛出来
        if (ce == null) {
            logger.error(e.getMessage(), e);
            return Result.errorWithArgs(Status.INTERNAL_SERVER_ERROR_ARGS, e.getMessage());
        }
        // 如果被注解，返回被注解的异常
        Status st = ce.value();
        logger.error(st.getMsg(), e);
        return Result.error(st);
    }

}

