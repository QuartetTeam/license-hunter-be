package quartet.server.api.certification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import quartet.server.api.certification.dto.response.CertificationResponse;
import quartet.server.api.certification.dto.response.CertificationCategoriesResponse;
import quartet.server.api.certification.dto.response.CertificationSearchResponse;
import quartet.server.api.certification.dto.response.CertificationsByCategoryResponse;
import quartet.server.api.certification.service.CertificationService;
import quartet.server.api.common.response.ApiResponse;
import java.util.List;

import static quartet.server.core.code.CommonSuccessCode.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/certifications")
public class CertificationController {

    private final CertificationService certificationService;

    // 대분류 카테고리 조회
    @GetMapping("/category")
    public ApiResponse<List<CertificationCategoriesResponse>> getCategories(
            @RequestParam(required = false) Boolean isDefault,
            @RequestParam(required = false) Long mainCategoryId
    ) {
        List<CertificationCategoriesResponse> categories;
        if (mainCategoryId != null) {
            categories = certificationService.getCategories(mainCategoryId);
        } else {
            categories = certificationService.getCategories(isDefault);
        }

        return ApiResponse.success(OK, categories);
    }

    // 자격증 상세 조회
    @GetMapping("/{certificationId}")
    public ApiResponse<CertificationResponse> getCertification(@PathVariable long certificationId) {
        CertificationResponse certificationResponse = certificationService.getCertification(certificationId);
        return ApiResponse.success(OK, certificationResponse);
    }

    // 특정 카테고리 자격증 조회
    @GetMapping("")
    public ApiResponse<Page<CertificationSearchResponse>> getAllCertificationByCategory(
            @RequestParam long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int pageSize
    ) {
        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Order.asc("id")));
        Page<CertificationSearchResponse> certifications = certificationService.getAllCertificationsByCategory(
                categoryId, pageable);
        return ApiResponse.success(OK, certifications);
    }

    @GetMapping("/recommendation")
    public ApiResponse<List<CertificationSearchResponse>> getRecommendedCertifications() {
        // @TODO 최지희: @AuthenticationPrincipal로 변경 예정
        long memberId = 1L;
        List<CertificationSearchResponse> recommendedCertifications = certificationService.getRecommendedCertifications(memberId);
        return ApiResponse.success(OK, recommendedCertifications);
    }

    @GetMapping("/search")
    public ApiResponse<List<CertificationSearchResponse>> getCertificationsBySearch(
            @RequestParam final String name) {
        List<CertificationSearchResponse> certifications = certificationService.getCertificationsBySearch(name);
        return ApiResponse.success(OK, certifications);
    }
}
