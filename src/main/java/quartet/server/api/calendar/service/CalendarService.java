package quartet.server.api.calendar.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quartet.server.api.calendar.dto.response.CalendarResponse;
import quartet.server.api.calendar.dto.response.ScheduleKey;
import quartet.server.api.calendar.query.CalendarQueryRepository;
import quartet.server.core.utils.DateUtils;
import quartet.server.domain.calender.exception.CalendarAlreadyExistsException;
import quartet.server.domain.calender.exception.CalendarNotFoundException;
import quartet.server.domain.calender.model.Calendar;
import quartet.server.domain.calender.repository.CalendarRepository;
import quartet.server.domain.certification.model.Certification;
import quartet.server.domain.certification.repository.CertificationRepository;
import quartet.server.domain.member.exception.MemberNotFoundException;
import quartet.server.domain.member.model.Member;
import quartet.server.domain.member.repository.MemberRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final MemberRepository memberRepository;
    private final CertificationRepository certificationRepository;
    private final CalendarQueryRepository calendarQueryRepository;

    public List<CalendarResponse> getCalendarsByMemberId(
            final long memberId,
            @NotNull final Instant startDate,
            @NotNull final Instant endDate) {
        List<CalendarResponse> calendarResponses = calendarQueryRepository.findCalendarResponsesByMemberId(memberId);

        List<Long> certificationIds = extractCertificationIds(calendarResponses);

        if (certificationIds.isEmpty()) {
            return Collections.emptyList();
        }

        final Instant dbStartDate = DateUtils.getDateBefore(DateUtils.toLocalDate(startDate),0, 1, 0);


        final Instant dbEndDate = DateUtils.getDateAfter(DateUtils.toLocalDate(endDate),0, 1, 0);
        System.out.println("========================");
        System.out.println("startDate = " + startDate);
        System.out.println("endDate = " + endDate);
        System.out.println("dbStartDate = " + dbStartDate);
        System.out.println("dbEndDate = " + dbEndDate);
        final Map<Long, Map<ScheduleKey, List<Instant>>> groupedSchedulesByCertificationId =
                calendarQueryRepository.findCalendarSchedulesByCertificationIdsAndDateRange(certificationIds, dbStartDate, dbEndDate);

        return convertToCalendarResponses(calendarResponses, groupedSchedulesByCertificationId, startDate, endDate);
    }

    private List<Long> extractCertificationIds(final List<CalendarResponse> responses) {
        return responses.stream()
                .map(CalendarResponse::certificationId)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<CalendarResponse> convertToCalendarResponses(
            final List<CalendarResponse> responses,
            final Map<Long, Map<ScheduleKey, List<Instant>>> groupedSchedulesByCertId,
            final Instant startDate,
            final Instant endDate) {

        return responses.stream()
                .map(response -> processCalendarResponse(response, groupedSchedulesByCertId, startDate, endDate))
                .filter(Objects::nonNull)
                .toList();
    }

    private CalendarResponse processCalendarResponse(
            final CalendarResponse response,
            final Map<Long, Map<ScheduleKey, List<Instant>>> groupedSchedulesByCertId,
            final Instant startDate,
            final Instant endDate) {

        final Map<ScheduleKey, List<Instant>> schedulesMap =
                groupedSchedulesByCertId.getOrDefault(response.certificationId(), Collections.emptyMap());

        final List<CalendarResponse.CalendarScheduleResponse> schedules =
                createFilteredSchedules(schedulesMap, startDate, endDate);

        if (schedules.isEmpty()) {
            return null;
        }

        return CalendarResponse.of(
                response.certificationId(),
                response.calendarId(),
                response.name(),
                schedules
        );
    }

    private List<CalendarResponse.CalendarScheduleResponse> createFilteredSchedules(
            final Map<ScheduleKey, List<Instant>> schedulesMap,
            final Instant startDate,
            final Instant endDate) {

        return schedulesMap.entrySet().stream()
                .map(entry -> createScheduleIfInRange(entry, startDate, endDate))
                .filter(Objects::nonNull)
                .toList();
    }

    private CalendarResponse.CalendarScheduleResponse createScheduleIfInRange(
            final Map.Entry<ScheduleKey, List<Instant>> entry,
            final Instant startDate,
            final Instant endDate) {

        final List<Instant> filteredDates = filterDatesByRange(entry.getValue(), startDate, endDate);

        if (filteredDates.isEmpty()) {
            return null;
        }

        return CalendarResponse.CalendarScheduleResponse.of(
                entry.getKey().scheduleType(),
                entry.getKey().examType().getValue(),
                entry.getKey().examRound(),
                filteredDates
        );
    }

    private List<Instant> filterDatesByRange(
            final List<Instant> dates,
            final Instant startDate,
            final Instant endDate) {
        boolean hasDateAfterStart = dates.stream().anyMatch(date -> !date.isBefore(startDate));
        boolean hasDateBeforeEnd = dates.stream().anyMatch(date -> !date.isAfter(endDate));

        if (hasDateAfterStart || hasDateBeforeEnd) {
            return dates.stream().sorted().toList();
        }

        return Collections.emptyList();
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