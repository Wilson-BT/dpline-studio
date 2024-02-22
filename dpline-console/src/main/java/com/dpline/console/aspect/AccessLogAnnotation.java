package com.dpline.console.aspect;

import java.lang.annotation.*;

/**
 * 用于过滤不需要的参数
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLogAnnotation {
    /**
     * 默认不需要打印的参数
     * @return
     */
    // ignore request args
    String[] ignoreRequestArgs() default {"loginUser"};

    /**
     * 默认打印Request的log
     * @return
     */
    boolean ignoreRequest() default false;

    /**
     * 默认忽略Response的log
     * @return
     */
    boolean ignoreResponse() default true;
}
