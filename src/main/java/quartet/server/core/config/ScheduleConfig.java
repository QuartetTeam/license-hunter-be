package quartet.server.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Clock;
import java.time.ZoneId;
import java.util.concurrent.ThreadFactory;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduleConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler().getScheduledExecutor());
    }

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("scheduled-task-");
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.setErrorHandler(t -> {
            log.error("스케줄 작업 실행 중 오류 발생: " + t.getMessage());
        });
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);

        scheduler.setClock(Clock.system(ZoneId.of("Asia/Seoul")));

        scheduler.setThreadFactory(this.virtualThreadFactory());

        return scheduler;
    }

    private ThreadFactory virtualThreadFactory() {
        return task -> Thread.ofVirtual()
                .name("v-scheduled-", 0)
                .unstarted(task);
    }
}