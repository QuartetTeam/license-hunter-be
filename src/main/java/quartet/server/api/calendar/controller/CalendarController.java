package quartet.server.api.calendar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import quartet.server.api.calendar.dto.response.CalendarResponse;
import quartet.server.api.calendar.service.CalendarService;
import quartet.server.api.common.response.ApiResponse;

import java.util.List;

import static quartet.server.core.code.CommonSuccessCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/calendars")
    public ApiResponse<List<CalendarResponse>> getCurrentMemberCalendars() {
        long memberId = 1L; // TODO 박현제: @AuthenticationPrincipal 로 변경 예정
        return ApiResponse.success(OK, calendarService.getCalendarsByMemberId(memberId));
    }

    @PostMapping("/certifications/{certificationId}/calendars")
    public ApiResponse<Void> subscribeCalendar(@PathVariable("certificationId") final long certificationId) {
        long memberId = 1L; // TODO 박현제: @AuthenticationPrincipal 로 변경 예정
        calendarService.subscribeCalendar(memberId, certificationId);
        return ApiResponse.success(CREATED);
    }

    @DeleteMapping("/certifications/{certificationId}/calendars")
    public ApiResponse<Void> unsubscribeCalendar(@PathVariable("certificationId") final long certificationId) {
        long memberId = 1L; // TODO: @AuthenticationPrincipal로 변경 예정
        calendarService.unsubscribeCalendar(certificationId, memberId);
        return ApiResponse.success(NO_CONTENT);
    }
}