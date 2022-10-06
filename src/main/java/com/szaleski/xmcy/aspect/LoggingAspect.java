package com.szaleski.xmcy.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@EnableAspectJAutoProxy
@ConditionalOnProperty(prefix = "crypto", value = "log-method-calls")
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.szaleski.xmcy.service.CryptoService.*(..))")
    public void executeOnServiceMethods() {
    }
    @Pointcut("execution(* com.szaleski.xmcy.controller.CryptoRestController.*(..))")
    public void executeOnControllerMethods() {
    }

    @Pointcut("@annotation(Loggable)")
    public void executeOnAnnotation() {
    }

    @Around(value = "executeOnServiceMethods()")
    public Object logMethodDuration(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        log.info(" :: Running service method '{}' -> {}[ms] :: ", joinPoint.getSignature().getName(), System.currentTimeMillis() - start);

        return result;
    }

    @Before("executeOnControllerMethods()")
    public void logMethodCall(JoinPoint joinPoint) {
        log.info(" :: Controller method '{}' with params {} :: ", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

}
