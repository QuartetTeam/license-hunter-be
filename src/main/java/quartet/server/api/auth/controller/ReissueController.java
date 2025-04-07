package quartet.server.api.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import quartet.server.api.auth.service.ReissueService;
import quartet.server.api.common.response.ApiResponse;

import static quartet.server.core.code.CommonSuccessCode.NO_CONTENT;
@RestController
@RequiredArgsConstructor
public class ReissueController {
    private final ReissueService reissueService;

    @PostMapping("/api/v1/reissue")
    public ApiResponse<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        reissueService.reissue(request, response);
        return ApiResponse.success(NO_CONTENT);
    }
}