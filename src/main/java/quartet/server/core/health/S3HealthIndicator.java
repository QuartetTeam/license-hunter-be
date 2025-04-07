package quartet.server.core.health;

import com.amazonaws.services.s3.AmazonS3;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3HealthIndicator implements HealthIndicator {

    private final AmazonS3 amazonS3;
    
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    
    @PostConstruct
    public void validateOnStartup() {
        Health health = health();
        if (health.getStatus() != Status.UP) {
            log.warn("S3 버킷 상태 확인 실패: {}", health.getDetails());
        } else {
            log.info("S3 버킷 [{}] 접근 확인 완료", bucketName);
        }
    }

    @Override
    public Health health() {
        try {
            long startTime = System.currentTimeMillis();
            boolean bucketExists = amazonS3.doesBucketExistV2(bucketName);
            long responseTime = System.currentTimeMillis() - startTime;
            
            if (bucketExists) {
                return Health.up()
                        .withDetail("bucketName", bucketName)
                        .withDetail("status", "accessible")
                        .withDetail("responseTime", responseTime + "ms")
                        .build();
            } else {
                return Health.down()
                        .withDetail("bucketName", bucketName)
                        .withDetail("reason", "Bucket does not exist")
                        .withDetail("responseTime", responseTime + "ms")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("bucketName", bucketName)
                    .withDetail("error", e.getClass().getName())
                    .withDetail("message", e.getMessage())
                    .build();
        }
    }
}