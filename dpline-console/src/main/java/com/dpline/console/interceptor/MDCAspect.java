package com.dpline.console.interceptor;

import com.dpline.common.Constants;
import com.dpline.common.enums.OperationsEnum;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.FileUtils;
import com.dpline.common.util.Result;
import com.dpline.common.util.TaskPathResolver;
import com.dpline.console.annotation.OperateTypeAnnotation;
import com.dpline.console.service.impl.DplineJobOperateLogImpl;
import com.dpline.console.util.AnnotationResolver;
import com.dpline.dao.rto.JobRto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;


/**
 *
 */
@Slf4j
@Aspect
@Component
public class MDCAspect {

    @Autowired
    DplineJobOperateLogImpl dplineJobOperateLogImpl;

    private static final Logger logger = LoggerFactory.getLogger(MDCAspect.class);

    @Pointcut(value = "@annotation(com.dpline.console.annotation.OperateTypeAnnotation)")
    private void mdcPointcut() {
    }

    @Around(value = "mdcPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        OperateTypeAnnotation annotation = methodSignature.getMethod().getAnnotation(OperateTypeAnnotation.class);
        OperationsEnum operationsEnum = annotation.value();
        JobRto jobRto = (JobRto) AnnotationResolver.newInstance().resolver(joinPoint, annotation.jobId());
        Long jobId = jobRto.getId();
        String traceId = UUID.randomUUID().toString().replace("-","");
        Object result = null;
        try {
            MDC.put(Constants.TRACE_ID, traceId);
            MDC.put(Constants.JOB_ID, jobId.toString());
            MDC.put(Constants.RUN_TYPE, operationsEnum.getOperateType());
            String operateLogPath = TaskPathResolver.getJobLogPath(operationsEnum.getOperateType(), jobId.toString(), traceId);
            dplineJobOperateLogImpl.createNewLog(jobId, operationsEnum, operateLogPath, traceId);
            FileUtils.truncateFile(operateLogPath);
            logger.info("Operation [{}] begin.", operationsEnum.getOperateType());
            // 必须要使用 joinPoint.getArgs,防止修改args后，不生效
            result = joinPoint.proceed(joinPoint.getArgs());
            if(Asserts.isNotNull(result) && result instanceof Result){
                Result<Object> res = (Result<Object>) result;
                if(res.getCode() != 200){
                    logger.error("Result:{}",res.getMsg());
                }
            }
            logger.info("Operation [{}] over. ", operationsEnum.getOperateType());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            MDC.clear();
        }
        return result;
    }

}
