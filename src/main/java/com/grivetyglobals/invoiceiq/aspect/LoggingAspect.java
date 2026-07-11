package com.grivetyglobals.invoiceiq.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LogManager.getLogger(this.getClass());

    /**
     * Pointcut that matches all repositories, services, and REST endpoints.
     */
    @Pointcut("within(com.grivetyglobals.invoiceiq.repository..*) " +
            "|| within(com.grivetyglobals.invoiceiq.service..*) " +
            "|| within(com.grivetyglobals.invoiceiq.controller..*)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut
    }

    /**
     * Advice that logs methods throwing exceptions.
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("❌ [ERROR] in {}.{}() with cause = '{}' and exception = '{}'",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                e.getCause() != null ? e.getCause() : "NULL",
                e.getMessage(),
                e);
    }

    /**
     * Advice that logs when a method is entered and exited.
     */
    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("📥 [ENTRY] Enter: {}.{}() with argument[s] = {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()));
        } else {
            // For general flow tracing at INFO level (reduced verbosity without args)
            log.info("📥 [ENTRY] Enter: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        }

        try {
            Object result = joinPoint.proceed();
            
            if (log.isDebugEnabled()) {
                log.debug("📤 [EXIT] Exit: {}.{}() with result = {}",
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(),
                        result);
            } else {
                log.info("📤 [EXIT] Exit: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            }
            
            return result;
        } catch (IllegalArgumentException e) {
            log.error("❌ [ERROR] Illegal argument: {} in {}.{}()",
                    Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
            throw e;
        }
    }
}
