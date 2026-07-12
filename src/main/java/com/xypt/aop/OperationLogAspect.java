package com.xypt.aop;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.xypt.annotation.OperLog;
import com.xypt.entity.OperationLog;
import com.xypt.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志AOP切面
 * 拦截所有带有 @OperLog 注解的方法，自动记录操作日志到数据库
 */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    @Autowired
    private OperationLogMapper operationLogMapper;

    /**
     * 环绕通知：拦截 @OperLog 注解的方法
     * 记录方法名、参数、执行时间、操作结果等信息
     */
    @Around("@annotation(com.xypt.annotation.OperLog)")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        OperationLog operationLog = new OperationLog();

        try {
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                // 获取客户端IP
                operationLog.setIp(getClientIp(request));
                // 获取请求方法和路径
                operationLog.setMethod(request.getMethod() + " " + request.getRequestURI());

                // 从Session中获取操作者用户名
                HttpSession session = request.getSession(false);
                if (session != null) {
                    Object adminUsername = session.getAttribute("adminUsername");
                    Object userUsername = session.getAttribute("username");
                    if (adminUsername != null) {
                        operationLog.setOperator(adminUsername.toString());
                    } else if (userUsername != null) {
                        operationLog.setOperator(userUsername.toString());
                    }
                }
            }

            // 获取注解中的操作描述
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperLog operLog = method.getAnnotation(OperLog.class);
            operationLog.setOperation(operLog.value());

            // 获取方法参数，进行安全序列化
            Object[] args = joinPoint.getArgs();
            // 过滤掉 HttpSession / HttpServletRequest / HttpServletResponse / MultipartFile
            // 这些 Spring 框架内部对象包含循环引用，用 Fastjson2 序列化会导致层级超过2048
            List<Object> safeArgs = Arrays.stream(args)
                .filter(arg -> arg != null
                    && !(arg instanceof HttpSession)
                    && !(arg instanceof HttpServletRequest)
                    && !(arg instanceof HttpServletResponse)
                    && !(arg instanceof MultipartFile))
                .collect(Collectors.toList());
            String params;
            try {
                // ReferenceDetection: 循环引用用 $ref 表示而非无限递归
                params = JSON.toJSONString(safeArgs, JSONWriter.Feature.ReferenceDetection);
            } catch (Exception ex) {
                // 序列化失败时降级为 toString()，不影响接口正常响应
                params = safeArgs.toString();
            }
            if (params.length() > 500) {
                params = params.substring(0, 500) + "...";
            }
            operationLog.setParams(params);

            // 执行目标方法
            Object result = joinPoint.proceed();

            // 记录成功状态
            operationLog.setStatus(1);
            long costTime = System.currentTimeMillis() - startTime;
            operationLog.setCostTime(costTime);
            operationLog.setCreateTime(LocalDateTime.now());

            // 异步保存日志（避免影响接口响应速度）
            operationLogMapper.insert(operationLog);

            return result;

        } catch (Exception e) {
            // 记录失败状态
            operationLog.setStatus(0);
            operationLog.setErrorMsg(e.getMessage());
            long costTime = System.currentTimeMillis() - startTime;
            operationLog.setCostTime(costTime);
            operationLog.setCreateTime(LocalDateTime.now());

            try {
                operationLogMapper.insert(operationLog);
            } catch (Exception ex) {
                log.error("保存操作日志失败", ex);
            }

            throw e;
        }
    }

    /**
     * 获取客户端真实IP（考虑代理情况）
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个IP时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
