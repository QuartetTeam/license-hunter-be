package quartet.server.api.certification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import quartet.server.api.certification.dto.response.CertificationRes;
import quartet.server.api.certification.dto.response.CertificationCategoriesRes;
import quartet.server.api.certification.dto.response.CertificationsByCategoryRes;
import quartet.server.api.common.response.ApiResponse;
import java.util.List;

import static quartet.server.core.code.CommonSuccessCode.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/certification")
public class CertificationController {

    private final CertificationService certificationService;

    // 대분류 카테고리 조회
    @GetMapping("/category")
    public ApiResponse<List<CertificationCategoriesRes>> getCategories(
            @RequestParam(required = false) Boolean isDefault,
            @RequestParam(required = false) Long parentId
    ){
        List<CertificationCategoriesRes> categoryList;
        if (parentId != null) categoryList = certificationService.getCategories(parentId);
        else categoryList = certificationService.getCategories(isDefault);

        return ApiResponse.success(OK, categoryList);
    }

    // 자격증 상세 조회
    @GetMapping("/{certificationId}")
    public ApiResponse<CertificationRes> getCertification(@PathVariable long certificationId){
        CertificationRes certificationRes= certificationService.getCertification(certificationId);
        return ApiResponse.success(OK, certificationRes);
    }

    // 특정 카테고리 자격증 조회
    @GetMapping("")
    public ApiResponse<Page<CertificationsByCategoryRes>>  getAllCertificationByCategory(
            @RequestParam long categoryId,
            @RequestParam boolean isSubCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int pageSize
    ){
        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Order.asc("id")));
        Page<CertificationsByCategoryRes> certificationList = certificationService.getAllCertificationsByCategory(
                categoryId, isSubCategory, pageable);
        return ApiResponse.success(OK,certificationList);
    }
}
