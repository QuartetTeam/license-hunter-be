package quartet.server.api.calendar.query;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import quartet.server.api.calendar.dto.response.CalendarResponse;
import quartet.server.api.calendar.dto.response.QCalendarResponse;
import quartet.server.api.calendar.dto.response.QCalendarResponse_CalendarScheduleResponse;
import quartet.server.domain.calender.model.QCalendar;
import quartet.server.domain.certification.model.QCertification;
import quartet.server.domain.certification.model.QCertificationSchedule;
import quartet.server.domain.member.model.QMember;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static quartet.server.domain.calender.model.QCalendar.calendar;
import static quartet.server.domain.certification.model.QCertificationSchedule.certificationSchedule;

@Repository
@RequiredArgsConstructor
public class CalendarQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<CalendarResponse> findCalendarsByMemberId(Long memberId) {
        QCalendar cal = calendar;
        QMember m = QMember.member;
        QCertification c = QCertification.certification;
        QCertificationSchedule cs = certificationSchedule;

        Map<Long, CalendarResponse> result = queryFactory
                .from(cal)
                .join(cal.member, m)
                .join(cal.certification, c)
                .leftJoin(cs).on(cs.certification.eq(c))
                .where(m.id.eq(memberId))
                .transform(GroupBy.groupBy(c.id).as(
                        new QCalendarResponse(
                                c.id,
                                c.name,
                                GroupBy.set(
                                        new QCalendarResponse_CalendarScheduleResponse(
                                                cs.scheduleType,
                                                cs.examType,
                                                cs.scheduledDate
                                        )
                                )
                        )
                ));

        return new ArrayList<>(result.values());
    }
}