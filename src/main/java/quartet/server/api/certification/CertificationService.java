package quartet.server.api.certification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quartet.server.api.certification.dto.response.CertificationCategoriesRes;
import quartet.server.api.certification.dto.response.CertificationRes;
import quartet.server.api.certification.dto.response.CertificationsByCategoryRes;
import quartet.server.api.certification.query.CertificationQueryRepository;
import quartet.server.domain.category.model.Category;
import quartet.server.domain.category.repository.CategoryRepository;
import quartet.server.domain.certification.repository.*;
import quartet.server.domain.certification.exception.CertificationNotFoundException;
import quartet.server.domain.category.exception.CategoryNotFoundException;
import quartet.server.domain.category.exception.SubCategoryNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificationService {
    private final AuthorityRepository authorityRepository;
    private final CertificationRepository certificationRepository;
    private final CertificationExamDetailRepository certificationExamRepository;
    private final CertificationPassCriteriaRepository certificationPassCriteriaRepository;
    private final CertificationScheduleRepository certificationScheduleRepository;
    private final CertificationViewLogRepository certificationViewLogRepository;
    private final CategoryRepository categoryRepository;

    private final CertificationQueryRepository certificationQueryRepository;

    @Transactional(readOnly = true)
    public CertificationRes getCertification(final long certificationId) {
        return certificationQueryRepository.getCertification(certificationId)
                 .orElseThrow(CertificationNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Page<CertificationsByCategoryRes> getAllCertificationsByCategory(
            long categoryId, final Pageable pageable){

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        // 대분류 카테고리가 주어질 경우 -> 디폴트 서브 카테고리로 변경한 후, 검색
        if (category.getParentCategory() == null) categoryId = certificationQueryRepository.getDefaultSubCategoryId(categoryId)
                .orElseThrow(SubCategoryNotFoundException::new);

        return certificationQueryRepository.findAllCertificationByCategory(categoryId,pageable);

    }

    // 소분류 카테고리 조회
    @Transactional(readOnly = true)
    public List<CertificationCategoriesRes> getCategories(final long parentId){
        List<Category> subCategoryList = categoryRepository.findByParentCategory_Id(parentId);

        return subCategoryList.stream()
                .map(category -> new CertificationCategoriesRes(category.getId(), category.getName()))
                .toList();
    }

    // 대분류 카테고리 조회
    @Transactional(readOnly = true)
    public List<CertificationCategoriesRes> getCategories(final boolean isDefault){
        List<Category> categoryList;
        if (isDefault) {
            categoryList = categoryRepository.findByIsDefaultTrue();
        } else {
            categoryList = categoryRepository.findByIsDefaultFalseAndParentCategoryIsNull();
        }

        return categoryList.stream()
                .map(category -> new CertificationCategoriesRes(category.getId(), category.getName()))
                .toList();
    }
}
