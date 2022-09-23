package com.szaleski.xmcy.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
@EnableAspectJAutoProxy
@ConditionalOnProperty(prefix = "crypto", value = "log-method-calls")
public class LoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.szaleski.xmcy.service.CryptoService.*(..))")
    public void executeOnServiceMethods() {
    }

    @Around(value = "executeOnServiceMethods()")
    public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        LOGGER.info(" :: Running method '{}' -> {}[ms] :: ", joinPoint.getSignature().getName(), System.currentTimeMillis() - start);

        return result;
    }

}
