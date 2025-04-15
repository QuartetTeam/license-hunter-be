package quartet.server.api.calendar.query;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import quartet.server.api.calendar.dto.response.CalendarResponse;
import quartet.server.api.calendar.dto.response.QCalendarResponse;
import quartet.server.api.calendar.dto.response.QScheduleKey;
import quartet.server.api.calendar.dto.response.ScheduleKey;
import quartet.server.domain.calender.model.QCalendar;
import quartet.server.domain.certification.model.QCertification;
import quartet.server.domain.certification.model.QCertificationSchedule;
import quartet.server.domain.certification.type.ScheduleGroup;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.querydsl.core.types.dsl.Expressions.cases;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CalendarQueryRepository {
    private final JPAQueryFactory queryFactory;
    public Map<Long, Map<ScheduleKey, List<Instant>>> findCalendarSchedulesByCertificationIdsAndDateRange(final List<Long> certificationIds,
                                                                                                          @NotNull final Instant startDate,
                                                                                                          @NotNull final Instant endDate) {
        QCertificationSchedule schedule = QCertificationSchedule.certificationSchedule;

        var scheduleTypeExpression = cases()
                .when(schedule.scheduleType.in(ScheduleGroup.APPLICATION.getScheduleTypes()))
                .then(Expressions.constant(ScheduleGroup.APPLICATION.getValue()))
                .when(schedule.scheduleType.in(ScheduleGroup.EXAM.getScheduleTypes()))
                .then(Expressions.constant(ScheduleGroup.EXAM.getValue()))
                .when(schedule.scheduleType.in(ScheduleGroup.PASS.getScheduleTypes()))
                .then(Expressions.constant(ScheduleGroup.PASS.getValue()))
                .otherwise(schedule.scheduleType.stringValue());

        if (certificationIds == null || certificationIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return queryFactory
                .from(schedule)
                .where(schedule.certification.id.in(certificationIds),
                        schedule.date.between(startDate, endDate)
                )
                .transform(
                        GroupBy.groupBy(schedule.certification.id)
                                .as(GroupBy.map(
                                        new QScheduleKey(
                                                scheduleTypeExpression,
                                                schedule.examType,
                                                schedule.examRound
                                        ),
                                        GroupBy.list(schedule.date)
                                ))
                );
    }

    public List<CalendarResponse> findCalendarResponsesByMemberId(final long memberId) {
        QCertification certification = QCertification.certification;
        QCalendar calendar = QCalendar.calendar;

        return queryFactory
                .select(
                        new QCalendarResponse(
                                certification.id,
                                calendar.id,
                                certification.name
                        ))
                .from(calendar)
                .join(calendar.certification, certification)
                .where(calendar.member.id.eq(memberId))
                .fetch();
    }
}