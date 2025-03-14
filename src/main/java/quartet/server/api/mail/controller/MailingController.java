package quartet.server.api.mail.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import quartet.server.api.common.response.ApiResponse;
import quartet.server.api.common.response.PageResponse;
import quartet.server.api.mail.dto.response.MailingResponse;
import quartet.server.api.mail.service.MailingService;

import java.util.List;

import static quartet.server.core.code.CommonSuccessCode.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MailingController {

    private final MailingService mailingService;

    @GetMapping("/mailings")
    public ApiResponse<PageResponse<MailingResponse>> getMemberMailing(
                                                                       @RequestParam(defaultValue = "0") final int page,
                                                                       @RequestParam(defaultValue = "4") final int pageSize) {
        long memberId = 1L; // TODO: @AuthenticationPrincipal로 변경 예정
        return ApiResponse.success(OK, PageResponse.from(mailingService.getMailingsByMemberId(memberId, PageRequest.of(page, pageSize))));
    }

    @PostMapping("/certifications/{certificationId}/mailings")
    public ApiResponse<MailingResponse> subscribeMailing(@PathVariable("certificationId") final long certificationId) {
        long memberId = 1L; // TODO: @AuthenticationPrincipal로 변경 예정
        mailingService.subscribeMailing(memberId, certificationId);
        return ApiResponse.success(CREATED);
    }

    @DeleteMapping("/mailings")
    public ApiResponse<Void> unsubscribeMailings(@RequestParam final List<Long> mailingIds) {
        long memberId = 1L; // TODO: @AuthenticationPrincipal로 변경 예정
        mailingService.unsubscribeMailings(memberId, mailingIds);
        return ApiResponse.success(NO_CONTENT);
    }
}