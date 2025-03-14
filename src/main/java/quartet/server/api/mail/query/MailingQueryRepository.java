package quartet.server.api.mail.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import quartet.server.api.mail.dto.response.MailingResponse;
import quartet.server.domain.certification.model.QCertification;
import quartet.server.domain.certification.model.QCertificationDescription;
import quartet.server.domain.mail.model.QMailing;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MailingQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<MailingResponse> getMailingsByMemberId(final long memberId, final Pageable pageable) {
        QMailing mailing = QMailing.mailing;
        QCertification certification = QCertification.certification;
        QCertificationDescription certDescription = QCertificationDescription.certificationDescription;

        long total = Optional.ofNullable(queryFactory
                        .select(mailing.count())
                        .from(mailing)
                        .where(mailing.member.id.eq(memberId))
                        .fetchOne())
                .orElse(0L);

        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, total);
        }

        List<MailingResponse> content = queryFactory
                .select(Projections.constructor(MailingResponse.class,
                        mailing.id,
                        certification.id,
                        certification.name,
                        certDescription.description))
                .from(mailing)
                .join(mailing.certification, certification)
                .leftJoin(certDescription).on(certification.id.eq(certDescription.certification.id))
                .where(mailing.member.id.eq(memberId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(mailing.createdAt.desc())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 곧 접수가 시작되는 자격증 알람 조회 (접수일 포함)
     */
//    public List<MailSubscriptionDetailResponse> findUpcomingApplicationAlarms(
//            final Long memberId,
//            final MailingStatus status,
//            final Instant startDate,
//            final Instant endDate) {
//
//        QMailAlarm mailAlarm = QMailAlarm.mailAlarm;
//        QCertification certification = QCertification.certification;
//        QAuthority authority = QAuthority.authority;
//        QCertificationSchedule schedule = QCertificationSchedule.certificationSchedule;
//
//        return queryFactory
//                .select(Projections.constructor(MailSubscriptionDetailResponse.class,
//                        mailAlarm.id,
//                        mailAlarm.member.id,
//                        certification.id,
//                        certification.name,
//                        authority.id,
//                        authority.name,
//                        mailAlarm.mailAlarmStatus,
//                        schedule.scheduledDate))
//                .from(mailAlarm)
//                .join(certification).on(mailAlarm.certification.id.eq(certification.id))
//                .join(authority).on(certification.authority.id.eq(authority.id))
//                .join(schedule).on(certification.id.eq(schedule.certification.id))
//                .where(
//                        mailAlarm.member.id.eq(memberId),
//                        mailAlarm.mailAlarmStatus.eq(status),
//                        schedule.scheduleType.eq(ScheduleType.APPLICATION_DATE),
//                        schedule.scheduledDate.between(startDate, endDate)
//                )
//                .orderBy(schedule.scheduledDate.asc())
//                .fetch();
//    }
}
