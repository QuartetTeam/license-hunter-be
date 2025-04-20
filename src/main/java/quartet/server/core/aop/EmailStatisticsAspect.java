package quartet.server.core.aop;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class EmailStatisticsAspect {
    
    private final Map<String, AtomicInteger> successCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> failureCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> totalCounts = new ConcurrentHashMap<>();
    
    @Around("@annotation(emailStats)")
    public Object collectEmailStatistics(ProceedingJoinPoint joinPoint, EmailSendingStatistics emailStats) throws Throwable {
        String category = emailStats.category();
        
        successCounts.computeIfAbsent(category, k -> new AtomicInteger(0));
        failureCounts.computeIfAbsent(category, k -> new AtomicInteger(0));
        totalCounts.computeIfAbsent(category, k -> new AtomicInteger(0));
        
        totalCounts.get(category).incrementAndGet();
        
        try {
            Object result = joinPoint.proceed();
            
            if (result instanceof Boolean && (Boolean) result) {
                successCounts.get(category).incrementAndGet();
            } else {
                failureCounts.get(category).incrementAndGet();
            }
            
            return result;
        } catch (Exception e) {
            failureCounts.get(category).incrementAndGet();
            throw e;
        }
    }
    
    @After("execution(* quartet.server.api.mail.service.MailingScheduleService.sendDailyNotifications())")
    public void logDailyStatistics() {
        int totalSuccess = successCounts.values().stream().mapToInt(AtomicInteger::get).sum();
        int totalFailures = failureCounts.values().stream().mapToInt(AtomicInteger::get).sum();
        int totalAttempts = totalCounts.values().stream().mapToInt(AtomicInteger::get).sum();
        
        log.info("일일 이메일 발송 통계:");
        successCounts.keySet().forEach(category -> {
            log.info("  - {}: 시도={}, 성공={}, 실패={}",
                    category,
                    totalCounts.get(category).get(),
                    successCounts.get(category).get(),
                    failureCounts.get(category).get());
        });
        
        log.info("총계: 시도={}, 성공={}, 실패={}, 성공률={}%",
                totalAttempts, totalSuccess, totalFailures,
                totalAttempts > 0 ? (totalSuccess * 100 / totalAttempts) : 0);

        successCounts.clear();
        failureCounts.clear();
        totalCounts.clear();
    }
}