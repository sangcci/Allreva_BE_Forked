package com.backend.allreva.common.logging;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    private static final String TRACE_ID = "traceId";
    private static final String SPAN_ID = "spanId";

    @Pointcut("execution(* com.backend.allreva..*Controller.*(..)) || "
            + "execution(* com.backend.allreva..*Service.*(..)) || "
            + "execution(* com.backend.allreva..*Repository.*(..)) || "
            + "execution(* com.backend.allreva..*Scheduler.*(..))")
    public void applicationLayer() {}

    @Around("applicationLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean isRootSpan = MDC.get(TRACE_ID) == null;

        if (isRootSpan) {
            MDC.put(TRACE_ID, generateShortId());
        }

        String spanId = generateShortId();
        MDC.put(SPAN_ID, spanId);

        long startTime = System.currentTimeMillis();
        String methodSignature = getSimpleSignature(joinPoint);

        try {
            if (isRootSpan) {
                log.info(">>> Start Request - {}", methodSignature);
            } else {
                log.info("→ {}", methodSignature);
            }

            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            if (isRootSpan) {
                log.info("<<< End Request - {} ({}ms)", methodSignature, executionTime);
            } else {
                log.info("← {} ({}ms)", methodSignature, executionTime);
            }

            return result;

        } catch (Exception ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("✗ {} failed ({}ms): {}", methodSignature, executionTime, ex.getMessage());
            throw ex;

        } finally {
            MDC.remove(SPAN_ID);
            if (isRootSpan) {
                MDC.remove(TRACE_ID);
            }
        }
    }

    private String getSimpleSignature(ProceedingJoinPoint joinPoint) {
        // Example: UserController.getUser() instead of full package path
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        return className + "." + methodName + "()";
    }

    private String generateShortId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
