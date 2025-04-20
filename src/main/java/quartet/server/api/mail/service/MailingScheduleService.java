package quartet.server.api.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quartet.server.api.mail.dto.response.*;
import quartet.server.api.mail.query.MailingQueryRepository;
import quartet.server.core.utils.DateUtils;
import quartet.server.domain.mail.type.MailingStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailingScheduleService {

    private final MailingQueryRepository mailingQueryRepository;
    private final EmailProcessingService emailProcessingService;

    private final int APPLICATION_NOTIFICATION_DAY_BEFORE = 1;
    private final int EXAM_NOTIFICATION_DAY_BEFORE = 3;

    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional(readOnly = true)
    public void sendDailyNotifications() {
        log.info("자격증 접수 알림 이메일 발송 스케줄러 실행");

        sendApplicationMail(DateUtils.today());

        sendExamMail(DateUtils.today());

        log.info("일일 이메일 발송 작업 완료");
    }

    private void sendExamMail(final LocalDate today) {
        // 특정 일수 후에 시험이 시작되는 날짜 계산
        LocalDate examDate = today.plusDays(EXAM_NOTIFICATION_DAY_BEFORE);
        Instant targetInstant = examDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<ExamMailResponse> examNotifications = mailingQueryRepository.findExamNotificationsForDate(
                targetInstant, MailingStatus.ACTIVE);

        examNotifications.forEach(emailProcessingService::processExamNotificationInIsolatedTransaction);
    }

    private void sendApplicationMail(final LocalDate today) {
        // 특정 일수 후에 접수가 시작되는 날짜 계산
        Instant targetDate = DateUtils.getDateAfter(today, 0, 0, APPLICATION_NOTIFICATION_DAY_BEFORE);

        List<ApplicationMailProjection> allTargets = mailingQueryRepository.findAllMailingTargetsForDate(
                targetDate, MailingStatus.ACTIVE);

        Map<Long, ApplicationMailResponse> memberMailings = groupTargetsByMemberId(allTargets);

        memberMailings.values().forEach(emailProcessingService::processApplicationNotificationInIsolatedTransaction);
    }

    private Map<Long, ApplicationMailResponse> groupTargetsByMemberId(final List<ApplicationMailProjection> targets) {
        Map<Long, ApplicationMailResponse> result = new HashMap<>();
        
        for (ApplicationMailProjection target : targets) {
            ApplicationMailResponse response = result.computeIfAbsent(
                target.memberId(), 
                id -> new ApplicationMailResponse(id, target.userName(), target.email())
            );

            ApplicationMailResponse.CertificationInfoResponse certInfo =
                    ApplicationMailResponse.CertificationInfoResponse.of(
                target.certificationId(),
                target.certificationName(),
                target.applicationDate(),
                target.applicationUrl(),
                target.examType()
            );

            response.addCertificationInfo(certInfo);
        }
        
        return result;
    }
}
