package quartet.server.api.calendar.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import quartet.server.api.calendar.dto.response.CalendarProjection;
import quartet.server.domain.calender.model.QCalendar;
import quartet.server.domain.certification.model.QCertification;
import quartet.server.domain.certification.model.QCertificationSchedule;
import quartet.server.domain.member.model.QMember;

import java.time.Instant;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class CalendarQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Stream<CalendarProjection> findCalendarProjectionsByMemberIdAndDateRange(final long memberId, final Instant startDate, final Instant endDate) {
        QCalendar calendar = QCalendar.calendar;
        QMember member = QMember.member;
        QCertification certification = QCertification.certification;
        QCertificationSchedule certificationSchedule = QCertificationSchedule.certificationSchedule;

        return queryFactory
                .select(Projections.constructor(
                        CalendarProjection.class,
                        certification.id,
                        calendar.id,
                        certification.name,
                        certificationSchedule.scheduleType,
                        certificationSchedule.examType,
                        certificationSchedule.examRound,
                        certificationSchedule.date
                ))
                .from(calendar)
                .join(calendar.member, member)
                .join(calendar.certification, certification)
                .leftJoin(certificationSchedule)
                .on(certificationSchedule.certification.eq(certification))
                .where(
                        member.id.eq(memberId)
                                .and(certificationSchedule.date.between(startDate, endDate))
                )
                .orderBy(certification.id.asc(), certificationSchedule.date.asc())
                .stream();
    }
}