package quartet.server.api.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import quartet.server.api.common.response.ApiResponse;
import quartet.server.api.member.dto.request.UpdateEmailRequest;
import quartet.server.api.member.dto.request.UpdateInterestsRequest;
import quartet.server.api.member.dto.request.UpdateNicknameRequest;
import quartet.server.api.member.dto.response.*;
import quartet.server.api.member.service.MemberService;
import quartet.server.core.security.userDetails.CustomUserDetails;

import static quartet.server.core.code.CommonSuccessCode.NO_CONTENT;
import static quartet.server.core.code.CommonSuccessCode.OK;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/me")
    public ApiResponse<MemberInfoResponse> getMyInfo(@AuthenticationPrincipal final CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        return ApiResponse.success(OK, memberService.getMyInfo(memberId));
    }

    @PatchMapping("/members/me/profile/nickname")
    public ApiResponse<MemberNicknameResponse> updateNickname(@AuthenticationPrincipal final CustomUserDetails userDetails,
                                                              @RequestBody final UpdateNicknameRequest request) {
        long memberId = userDetails.getMemberId();
        return ApiResponse.success(OK, memberService.updateNickname(memberId, request.nickname()));
    }

    @PatchMapping("/members/me/profile/email")
    public ApiResponse<MemberEmailResponse> updateEmail(@AuthenticationPrincipal final CustomUserDetails userDetails,
                                                        @RequestBody final UpdateEmailRequest request) {
        long memberId = userDetails.getMemberId();
        return ApiResponse.success(OK, memberService.updateEmail(memberId, request.email()));
    }

    @PostMapping("/members/me/profile/profile-image")
    public ApiResponse<MemberProfileImageResponse> uploadProfileImage(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @RequestPart("image") final MultipartFile image) {

        long memberId = userDetails.getMemberId();
        return ApiResponse.success(OK, memberService.updateProfileImage(memberId, image));
    }

    @PatchMapping("/members/me/profile/interests")
    public ApiResponse<MemberInterestResponse> updateInterests(@AuthenticationPrincipal final CustomUserDetails userDetails,
                                                               @RequestBody final UpdateInterestsRequest request) {
        long memberId = userDetails.getMemberId();
        return ApiResponse.success(OK, memberService.updateInterests(memberId, request.categoryIds()));
    }

    @PatchMapping("/members/me/settings/mailing-status")
    public ApiResponse<MemberMailingStatusResponse> updateMailingStatus(@AuthenticationPrincipal final CustomUserDetails userDetails) {
        long memberId = userDetails.getMemberId();
        return ApiResponse.success(OK, memberService.updateMailingStatus(memberId));
    }

    @DeleteMapping("/members/me/settings/delete-account")
    public ApiResponse<Void> deleteAccount(@AuthenticationPrincipal final CustomUserDetails userDetails) {
        long memberId = userDetails.getMemberId();
        memberService.deleteMember(memberId);
        return ApiResponse.success(NO_CONTENT);
    }
}