package quartet.server.api.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import quartet.server.api.common.response.ApiResponse;
import quartet.server.api.member.dto.response.MemberMailingStatusResponse;
import quartet.server.api.member.service.MemberService;

import static quartet.server.core.code.CommonSuccessCode.OK;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/me/mailing-status")
    public ApiResponse<MemberMailingStatusResponse> getMailingStatus() {
        long memberId = 1L; // TODO: @AuthenticationPrincipal로 변경 예정
        return ApiResponse.success(OK, memberService.getMailingStatus(memberId));
    }

    @PatchMapping("/members/me/mailing-status")
    public ApiResponse<MemberMailingStatusResponse> updateMailingStatus() {
        long memberId = 1L; // TODO: @AuthenticationPrincipal로 변경 예정
        return ApiResponse.success(OK, memberService.updateMailingStatus(memberId));
    }
}
