package quartet.server.api.certification;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import quartet.server.api.certification.dto.response.CertificationRes;
import quartet.server.api.common.response.ApiResponse;

import static quartet.server.core.code.CommonSuccessCode.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/certification")
public class CertificationController {

    private final CertificationService certificationService;

    // 상세 조회
    @GetMapping("/{certificationId}")
    public ApiResponse<CertificationRes> getCertification(@PathVariable long certificationId){
        CertificationRes certificationRes= certificationService.getCertification(certificationId);
        return ApiResponse.success(OK, certificationRes);
    }


}
