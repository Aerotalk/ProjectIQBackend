package com.grivetyglobals.invoiceiq.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceMonitoringAspect {

    private final Logger log = LogManager.getLogger(this.getClass());
    
    // Threshold in milliseconds to flag a method as SLOW
    private static final long SLOW_EXECUTION_THRESHOLD_MS = 500;

    /**
     * Monitor execution time of any service or controller method.
     */
    @Around("within(com.grivetyglobals.invoiceiq.service..*) || within(com.grivetyglobals.invoiceiq.controller..*)")
    public Object monitorExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        
        try {
            return joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - start;
            
            if (executionTime > SLOW_EXECUTION_THRESHOLD_MS) {
                log.warn("🐌 [SLOW] Method {}.{}() executed in {} ms (Exceeded {} ms threshold)",
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(),
                        executionTime,
                        SLOW_EXECUTION_THRESHOLD_MS);
            } else if (log.isDebugEnabled()) {
                log.debug("⏱️ [TIME] Method {}.{}() executed in {} ms",
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(),
                        executionTime);
            }
        }
    }
}
