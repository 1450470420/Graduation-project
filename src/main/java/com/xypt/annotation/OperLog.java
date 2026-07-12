package com.xypt.annotation;

import java.lang.annotation.*;

/**
 * 操作日志记录注解
 * 使用AOP切面拦截带有此注解的方法，自动记录操作日志
 * 使用方式：在Controller方法上添加 @OperLog(value = "操作说明")
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {

    /**
     * 操作描述
     */
    String value() default "";
}
