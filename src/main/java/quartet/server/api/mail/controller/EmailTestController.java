package quartet.server.api.mail.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import quartet.server.api.common.response.ApiResponse;
import quartet.server.api.mail.service.MailingScheduleService;

import static quartet.server.core.code.CommonSuccessCode.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/mailings")
@RequiredArgsConstructor
public class EmailTestController { // todo: 테스트 후 삭제 예정

    private final MailingScheduleService mailingScheduleService;

    /**
     * 이메일 전송 직접 테스트
     */
    @PostMapping("/schedule")
    public ApiResponse<Void> sendWelcomeEmail() {
        mailingScheduleService.sendDailyNotifications();
        return ApiResponse.success(NO_CONTENT);
    }
}