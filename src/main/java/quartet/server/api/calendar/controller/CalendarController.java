package quartet.server.api.calendar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import quartet.server.api.calendar.dto.response.CalendarResponse;
import quartet.server.api.calendar.service.CalendarService;
import quartet.server.api.common.response.ApiResponse;
import quartet.server.core.security.userDetails.CustomUserDetails;
import quartet.server.core.utils.DateUtils;

import java.util.List;

import static quartet.server.core.code.CommonSuccessCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CalendarController {

    private final CalendarService calendarService;
    @GetMapping("/calendars")
    public ApiResponse<List<CalendarResponse>> getMemberCalendars(@AuthenticationPrincipal final CustomUserDetails userDetails) {
        return ApiResponse.success(OK, calendarService.getCalendarsByMemberId(userDetails.getMemberId(), DateUtils.getFirstDayOfMonth(DateUtils.now()), DateUtils.getLastDayOfMonth(DateUtils.getDateAfterNow(0,11,0))));
    }

    @PostMapping("/certifications/{certificationId}/calendars")
    public ApiResponse<Void> subscribeCalendar(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @PathVariable("certificationId") final long certificationId) {
        calendarService.subscribeCalendar(userDetails.getMemberId(), certificationId);
        return ApiResponse.success(CREATED);
    }

    @DeleteMapping("/certifications/{certificationId}/calendars")
    public ApiResponse<Void> unsubscribeCalendar(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @PathVariable("certificationId") final long certificationId) {
        calendarService.unsubscribeCalendar(userDetails.getMemberId(), certificationId);
        return ApiResponse.success(NO_CONTENT);
    }
}