package quartet.server.api.mail.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import quartet.server.api.common.response.ApiResponse;
import quartet.server.api.common.response.PageResponse;
import quartet.server.api.mail.dto.response.MailingResponse;
import quartet.server.api.mail.service.MailingService;
import quartet.server.core.security.userDetails.CustomUserDetails;

import java.util.List;

import static quartet.server.core.code.CommonSuccessCode.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MailingController {

    private final MailingService mailingService;

    @GetMapping("/mailings")
    public ApiResponse<PageResponse<MailingResponse>> getMemberMailing(@AuthenticationPrincipal final CustomUserDetails userDetails,
                                                                       @RequestParam(defaultValue = "0") final int page,
                                                                       @RequestParam(defaultValue = "4") final int pageSize) {
        return ApiResponse.success(OK, PageResponse.from(mailingService.getMailingsByMemberId(userDetails.getMemberId(), PageRequest.of(page, pageSize))));
    }

    @PostMapping("/certifications/{certificationId}/mailings")
    public ApiResponse<MailingResponse> subscribeMailing(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @PathVariable("certificationId") final long certificationId) {
        mailingService.subscribeMailing(userDetails.getMemberId(), certificationId);
        return ApiResponse.success(CREATED);
    }

    @DeleteMapping("/mailings")
    public ApiResponse<Void> unsubscribeMailings(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @RequestParam final List<Long> mailingIds) {
        mailingService.unsubscribeMailings(userDetails.getMemberId(), mailingIds);
        return ApiResponse.success(NO_CONTENT);
    }
}