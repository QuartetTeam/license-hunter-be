package quartet.server.api.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import quartet.server.api.mail.dto.response.ExamMailResponse;
import quartet.server.api.mail.dto.response.ApplicationMailResponse;
import quartet.server.core.aop.EmailSendingStatistics;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// AOP 인터셉트가 필요한 메서드를 위한 새로운 서비스
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProcessingService {

    private final EmailService emailService;

    private final int APPLICATION_NOTIFICATION_DAY_BEFORE = 1;
    private final int EXAM_NOTIFICATION_DAY_BEFORE = 3;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EmailSendingStatistics(category = "exam")
    public boolean processExamNotificationInIsolatedTransaction(final ExamMailResponse response) {
        try {
            sendExamReminderEmail(response);
            return true;
        } catch (Exception e) {
            log.error("시험 알림 이메일 발송 실패: memberId={}, certificationId={}",
                    response.memberId(), response.certificationId(), e);
            return false;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EmailSendingStatistics(category = "application")
    public boolean processApplicationNotificationInIsolatedTransaction(final ApplicationMailResponse response) {
        try {
            sendApplicationReminderEmail(response);
            return true;
        } catch (Exception e) {
            log.error("통합 자격증 접수 알림 이메일 발송 실패: memberId={}",
                    response.memberId(), e);
            return false;
        }
    }

    private void sendApplicationReminderEmail(final ApplicationMailResponse memberMailing) {
        // 이메일 제목: 여러 자격증이 있을 경우 표시 방법 변경
        String subject;
        final int certificationCount = memberMailing.certifications().size();
        if (certificationCount > 1) {
            subject = String.format("[자격저격] %d개 자격증 접수일 알림",
                    certificationCount, APPLICATION_NOTIFICATION_DAY_BEFORE);
        } else {
            subject = String.format("[자격저격] %s 접수일 알림", memberMailing.certifications().getFirst().certificationName(), EXAM_NOTIFICATION_DAY_BEFORE);
        }

        Map<String, Object> contextData = createApplicationEmailContext(memberMailing);

        emailService.sendTemplateEmail(memberMailing.email(), subject, "receipt-day", contextData);
    }

    private Map<String, Object> createApplicationEmailContext(ApplicationMailResponse response) {
        Map<String, Object> contextData = new HashMap<>();
        contextData.put("userName", response.userName());
        contextData.put("dDay", String.valueOf(APPLICATION_NOTIFICATION_DAY_BEFORE));
        contextData.put("certifications", response.certifications().stream()
                .map(cert -> Map.of(
                        "certificationName", cert.certificationName() + " " + cert.examType().getValue(),
                        "applicationDate", cert.date().atZone(ZoneId.systemDefault()).toLocalDate().toString(),
                        "detailLink", "https://www.quartet.com/certifications/" + cert.certificationId(), //todo 박현제: 최종 결정된 프론트 URL에 따라 수정
                        "applicationUrl", cert.applicationUrl()
                ))
                .collect(Collectors.toList())
        );
        return contextData;
    }

    private void sendExamReminderEmail(final ExamMailResponse response) {
        final String subject = String.format("[자격저격] %s 시험 %d일 전 알림", response.certificationName(), EXAM_NOTIFICATION_DAY_BEFORE);

        Map<String, Object> contextData = createExamEmailContext(response);

        emailService.sendTemplateEmail(response.email(), subject, "exam-day", contextData);
    }

    private Map<String, Object> createExamEmailContext(ExamMailResponse notification) {
        Map<String, Object> contextData = new HashMap<>();
        contextData.put("userName", notification.userName());
        contextData.put("examName", notification.certificationName());
        contextData.put("examDate", notification.examDate().atZone(ZoneId.systemDefault()).toLocalDate().toString());
        contextData.put("dDay", String.valueOf(EXAM_NOTIFICATION_DAY_BEFORE));
        contextData.put("detailLink", "https://www.quartet.com/certifications/" + notification.certificationId()); //todo 박현제: 최종 결정된 프론트 URL에 따라 수정

        return contextData;
    }
}
