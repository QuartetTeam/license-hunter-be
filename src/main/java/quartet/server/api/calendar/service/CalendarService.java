package quartet.server.api.calendar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quartet.server.api.calendar.dto.response.CalendarProjection;
import quartet.server.api.calendar.dto.response.CalendarResponse;
import quartet.server.api.calendar.query.CalendarQueryRepository;
import quartet.server.domain.calender.exception.CalendarAlreadyExistsException;
import quartet.server.domain.calender.exception.CalendarNotFoundException;
import quartet.server.domain.calender.model.Calendar;
import quartet.server.domain.calender.repository.CalendarRepository;
import quartet.server.domain.certification.model.Certification;
import quartet.server.domain.certification.repository.CertificationRepository;
import quartet.server.domain.certification.type.ScheduleGroup;
import quartet.server.domain.member.exception.MemberNotFoundException;
import quartet.server.domain.member.model.Member;
import quartet.server.domain.member.repository.MemberRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final MemberRepository memberRepository;
    private final CertificationRepository certificationRepository;
    private final CalendarQueryRepository calendarQueryRepository;
    private final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    public List<CalendarResponse> getCalendarsByMemberId(final long memberId, final LocalDate baseDate) {

        final Instant dbStartDate = baseDate.minusMonths(2)
                .atStartOfDay(SEOUL_ZONE)
                .toInstant();
        final Instant dbEndDate = baseDate.plusYears(1)
                .plusMonths(2)
                .atStartOfDay(SEOUL_ZONE)
                .toInstant();

        final Instant filterStartDate = baseDate.with(TemporalAdjusters.firstDayOfMonth()) //todo 박현제: dateUtil로 리팩터링 하기
                .atStartOfDay(SEOUL_ZONE)
                .toInstant();
        final Instant filterEndDate = baseDate.plusYears(1)
                .with(TemporalAdjusters.lastDayOfMonth())
                .atStartOfDay(SEOUL_ZONE)
                .toInstant();

        try (Stream<CalendarProjection> projectionStream = calendarQueryRepository
                .findCalendarProjectionsByMemberIdAndDateRange(memberId, dbStartDate, dbEndDate)) {

            final Map<Long, List<CalendarProjection>> certificationGroups = projectionStream
                    .collect(Collectors.groupingBy(CalendarProjection::certificationId));

            return convertToCalendarResponses(certificationGroups, filterStartDate, filterEndDate);
        } catch (Exception e) {
            log.error("[CalendarService] : 캘린더 오류 - {}", e.getMessage(), e);
            throw new RuntimeException("캘린더 조회 중 오류가 발생했습니다.");
        }
    }

    private List<CalendarResponse> convertToCalendarResponses(final Map<Long, List<CalendarProjection>> certificationGroups,
                                                              final Instant filterStartDate,
                                                              final Instant filterEndDate) {

        return certificationGroups.entrySet().stream()
                .map(entry -> createCalendarResponse(entry.getKey(), entry.getValue(), filterStartDate, filterEndDate))
                .filter(response -> !response.schedules().isEmpty())
                .collect(Collectors.toList());
    }

    private CalendarResponse createCalendarResponse(final long certificationId,
                                                    final List<CalendarProjection> projections,
                                                    final Instant todayStart,
                                                    final Instant filterEndDate) {
        final CalendarProjection firstProj = projections.getFirst();

        final List<CalendarResponse.CalendarScheduleResponse> schedules = toCalendarScheduleResponses(projections, todayStart, filterEndDate);
        return CalendarResponse.of(
                certificationId,
                firstProj.calendarId(),
                firstProj.certificationName(),
                schedules
        );
    }

    private ScheduleKey createScheduleKey(final CalendarProjection proj) {
        return new ScheduleKey(
                ScheduleGroup.findByScheduleType(proj.scheduleType()).getValue(),
                proj.examType() != null ? proj.examType().getValue() : null,
                proj.examRound()
        );
    }

    private record ScheduleKey(
            String scheduleGroup,
            String examType,
            String examRound
    ) {}

    public List<CalendarResponse.CalendarScheduleResponse> toCalendarScheduleResponses(final List<CalendarProjection> projections, final Instant todayStart, final Instant filterEndDate) {
        final Map<ScheduleKey, List<CalendarProjection>> grouped = groupByScheduleKey(projections);
        final Map<ScheduleKey, List<CalendarProjection>> filtered = filterByDateRange(grouped, todayStart, filterEndDate);
        return convertToCalendarScheduleResponses(filtered);
    }

    private Map<ScheduleKey, List<CalendarProjection>> groupByScheduleKey(final List<CalendarProjection> projections) {
        return projections.stream()
                .collect(Collectors.groupingBy(this::createScheduleKey));
    }

    private Map<ScheduleKey, List<CalendarProjection>> filterByDateRange(
            final Map<ScheduleKey, List<CalendarProjection>> grouped,
            final Instant todayStart,
            final Instant filterEndDate
    ) {
        return grouped.entrySet().stream()
                .filter(entry -> entry.getValue().stream()
                        .anyMatch(proj -> isWithinRange(proj.date(), todayStart, filterEndDate)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean isWithinRange(final Instant date, final Instant start, final Instant end) {
        return (date.equals(start) || date.isAfter(start)) &&
                (date.equals(end) || date.isBefore(end));
    }

    private List<CalendarResponse.CalendarScheduleResponse> convertToCalendarScheduleResponses(final Map<ScheduleKey, List<CalendarProjection>> groupedProjections) {
        return groupedProjections.entrySet().stream()
                .map(entry -> {
                    ScheduleKey key = entry.getKey();
                    List<Instant> dates = entry.getValue().stream()
                            .map(CalendarProjection::date)
                            .collect(Collectors.toList());
                    return CalendarResponse.CalendarScheduleResponse.of(
                            key.scheduleGroup,
                            key.examType,
                            key.examRound,
                            dates
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void subscribeCalendar(final long memberId, final long certificationId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        final Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(CalendarNotFoundException::new);

        if (calendarRepository.existsByMemberIdAndCertificationId(member.getId(), certification.getId())) {
            throw new CalendarAlreadyExistsException();
        }

        final Calendar calendar = Calendar.of(member, certification);

        calendarRepository.save(calendar);
    }

    @Transactional
    public void unsubscribeCalendar(final long memberId, final long certificationId) {
        final Calendar calendar = calendarRepository.findByMemberIdAndCertificationId(memberId, certificationId)
                .orElseThrow(CalendarNotFoundException::new);

        calendarRepository.delete(calendar);
    }
}