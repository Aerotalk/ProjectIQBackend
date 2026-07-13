package com.grivetyglobals.invoiceiq.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper mapper;

    public LoggingAspect() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointcut() {}

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void servicePointcut() {}

    @Around("controllerPointcut()")
    public Object logControllerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        log.info("🚀 [CONTROLLER_ENTRY] Initiated request to {}.{}", className, methodName);
        
        try {
            List<Object> safeArgs = new ArrayList<>();
            for (Object arg : args) {
                if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse || arg instanceof MultipartFile) {
                    safeArgs.add("[" + arg.getClass().getSimpleName() + "]");
                } else {
                    safeArgs.add(arg);
                }
            }
            log.info("📦 [PAYLOAD] Input Arguments: {}", mapper.writeValueAsString(safeArgs));
        } catch (Exception e) {
            log.info("📦 [PAYLOAD] Input Arguments: [Cannot serialize payload: {}]", e.getMessage());
        }

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            
            log.info("✨ [CONTROLLER_SUCCESS] Flawless execution in {}.{}", className, methodName);
            try {
                log.info("🎁 [RESULT_DATA] Outgoing Response: {}", mapper.writeValueAsString(result));
            } catch (Exception e) {
                log.info("🎁 [RESULT_DATA] Outgoing Response: [Cannot serialize response]");
            }
            log.info("⏱️ [PERFORMANCE] Total API Time: {} ms", elapsedTime);
            
            return result;
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ [WARNING/BAD_REQUEST] Business logic issue in {}.{}: {}", className, methodName, e.getMessage());
            throw e;
        } catch (Throwable e) {
            long elapsedTime = System.currentTimeMillis() - start;
            log.error("💥 [CRASH/FAILURE] Critical error in {}.{}", className, methodName);
            log.error("🔍 [ROOT_CAUSE] Reason for failure: {}", e.getMessage(), e);
            log.error("⏱️ [PERFORMANCE] Failed abruptly after {} ms", elapsedTime);
            throw e;
        }
    }

    @Around("servicePointcut()")
    public Object logServiceAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        
        log.info("⚙️ [SERVICE_ENTRY] Processing logic in {}.{}", className, methodName);
        long start = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            log.info("✅ [SERVICE_SUCCESS] Completed {}.{} in ⏱️ {} ms", className, methodName, elapsedTime);
            return result;
        } catch (Throwable e) {
            long elapsedTime = System.currentTimeMillis() - start;
            log.error("❌ [SERVICE_EXCEPTION] Error thrown in {}.{} after ⏱️ {} ms", className, methodName, elapsedTime);
            log.error("🔍 [ROOT_CAUSE] Details: {}", e.getMessage());
            throw e;
        }
    }
}
