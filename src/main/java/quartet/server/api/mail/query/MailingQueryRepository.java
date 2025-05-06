package quartet.server.api.mail.query;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import quartet.server.api.mail.dto.response.*;
import quartet.server.core.utils.DateUtils;
import quartet.server.domain.certification.model.QAuthority;
import quartet.server.domain.certification.model.QCertification;
import quartet.server.domain.certification.model.QCertificationSchedule;
import quartet.server.domain.certification.type.ScheduleType;
import quartet.server.domain.mail.model.QMailing;
import quartet.server.domain.mail.type.MailingStatus;
import quartet.server.domain.member.model.QMember;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.dsl.Expressions.cases;

@Repository
@RequiredArgsConstructor
public class MailingQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<MailingResponse> getMailingsByMemberId(final long memberId, final LocalDateTime startDate, final Pageable pageable) {
        QMailing mailing = QMailing.mailing;
        QCertification certification = QCertification.certification;
        QCertificationSchedule applicationSchedule = new QCertificationSchedule("applicationSchedule");
        QCertificationSchedule examSchedule = new QCertificationSchedule("examSchedule");

        final long total = Optional.ofNullable(queryFactory
                        .select(mailing.count())
                        .from(mailing)
                        .where(mailing.member.id.eq(memberId))
                        .fetchOne())
                .orElse(0L);

        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, total);
        }

        List<MailingResponse> content = queryFactory
                .from(mailing)
                .join(mailing.certification, certification)
                .leftJoin(applicationSchedule).on(
                        applicationSchedule.certification.eq(certification)
                                .and(applicationSchedule.date.after(startDate))
                                .and(applicationSchedule.scheduleType.eq(ScheduleType.APPLICATION_START))
                )
                .leftJoin(examSchedule).on(
                        examSchedule.certification.eq(certification)
                                .and(examSchedule.date.after(startDate))
                                .and(examSchedule.scheduleType.eq(ScheduleType.EXAM_START))
                )
                .where(mailing.member.id.eq(memberId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(mailing.createdAt.asc())
                .transform(GroupBy.groupBy(mailing.id).list(
                        new QMailingResponse(
                                mailing.id,
                                certification.id,
                                certification.name,
                                cases()
                                        .when(applicationSchedule.id.isNotNull())
                                        .then(GroupBy.min(applicationSchedule.date))
                                        .otherwise(Expressions.nullExpression(LocalDateTime.class)),
                                cases()
                                        .when(examSchedule.id.isNotNull())
                                        .then(GroupBy.min(examSchedule.date))
                                        .otherwise(Expressions.nullExpression(LocalDateTime.class))
                        )
                ));

        return new PageImpl<>(content, pageable, total);
    }

    public List<ApplicationMailProjection> findAllMailingTargetsForDate(
            final LocalDateTime targetDate, final MailingStatus requiredMailingStatus) {

        QMailing mailing = QMailing.mailing;
        QMember member = QMember.member;
        QCertification certification = QCertification.certification;
        QCertificationSchedule schedule = QCertificationSchedule.certificationSchedule;
        QAuthority authority = QAuthority.authority;

        LocalDateTime startOfDay = DateUtils.getDayStart(targetDate);
        LocalDateTime endOfDay = DateUtils.getDayEnd(targetDate);

        return queryFactory
                .select(new QApplicationMailProjection(
                        member.id,
                        certification.id,
                        member.nickname,
                        member.email,
                        certification.name,
                        schedule.date,
                        authority.applicationUrl,
                        schedule.examType))
                .from(mailing)
                .join(mailing.member, member)
                .join(mailing.certification, certification)
                .join(certification.authority, authority)
                .join(certification.schedules, schedule)
                .where(
                        member.mailingStatus.eq(requiredMailingStatus)
                                .and(schedule.scheduleType.eq(ScheduleType.APPLICATION_START))
//                                // examType이 null이거나 WRITTEN인 경우만 포함
//                                .and(schedule.examType.isNull().or(schedule.examType.eq(ExamType.WRITTEN))) // todo 박현제: 추후에 미포함 여부 검토
                                .and(schedule.date.between(startOfDay, endOfDay))
                )
                .orderBy(member.id.asc())
                .fetch();
        }

    public List<ExamMailResponse> findExamNotificationsForDate(
            final LocalDateTime targetDate, final MailingStatus requiredMailingStatus) {

        QMailing mailing = QMailing.mailing;
        QMember member = QMember.member;
        QCertification certification = QCertification.certification;
        QCertificationSchedule schedule = QCertificationSchedule.certificationSchedule;

        LocalDateTime startOfDay = DateUtils.getDayStart(targetDate);
        LocalDateTime endOfDay = DateUtils.getDayEnd(targetDate);

        return queryFactory
                .select(new QExamMailResponse(
                        member.id,
                        certification.id,
                        member.nickname,
                        member.email,
                        certification.name,
                        schedule.date,
                        schedule.examType))
                .from(mailing)
                .join(mailing.member, member)
                .join(mailing.certification, certification)
                .join(certification.schedules, schedule)
                .where(
                        member.mailingStatus.eq(requiredMailingStatus)
                                .and(schedule.scheduleType.eq(ScheduleType.EXAM_START))
                                .and(schedule.date.between(startOfDay, endOfDay))
                )
                .fetch();
    }
}