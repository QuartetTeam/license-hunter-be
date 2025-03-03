package quartet.server.api.certification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import quartet.server.api.certification.dto.response.CertificationResponse;
import quartet.server.api.certification.dto.response.CertificationsByCategoryResponse;
import quartet.server.api.certification.query.CategoryQueryRepository;
import quartet.server.api.certification.dto.response.CertificationCategoriesResponse;
import quartet.server.api.certification.query.CertificationQueryRepository;
import quartet.server.core.utils.RandomGenerator;
import quartet.server.domain.category.model.Category;
import quartet.server.domain.category.repository.CategoryRepository;
import quartet.server.domain.certification.repository.*;
import quartet.server.domain.certification.exception.CertificationNotFoundException;
import quartet.server.domain.category.exception.CategoryNotFoundException;
import quartet.server.domain.category.exception.SubCategoryNotFoundException;
import quartet.server.domain.member.repository.MemberCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificationService {
    private final MemberCategoryRepository memberCategoryRepository;
    private final AuthorityRepository authorityRepository;
    private final CertificationRepository certificationRepository;
    private final CertificationExamDetailRepository certificationExamRepository;
    private final CertificationPassCriteriaRepository certificationPassCriteriaRepository;
    private final CertificationScheduleRepository certificationScheduleRepository;
    private final CertificationViewLogRepository certificationViewLogRepository;
    private final CategoryRepository categoryRepository;

    private final CategoryQueryRepository categoryQueryRepository;
    private final CertificationQueryRepository certificationQueryRepository;

    private final RandomGenerator randomGenerator;

    @Transactional(readOnly = true)
    public CertificationResponse getCertification(final long certificationId) {
        return certificationQueryRepository.getCertification(certificationId)
                 .orElseThrow(CertificationNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Page<CertificationsByCategoryResponse> getAllCertificationsByCategory(
            long categoryId, final Pageable pageable){

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);


        if (category.getParentCategory() == null) categoryId = categoryQueryRepository.getDefaultSubCategoryId(categoryId)
                .orElseThrow(SubCategoryNotFoundException::new);

        return certificationQueryRepository.findAllCertificationByCategory(categoryId,pageable);

    }

    @Transactional(readOnly = true)
    public List<CertificationCategoriesResponse> getCategories(final long parentId){
        List<Category> subCategories = categoryRepository.findByParentCategory_Id(parentId);

        return subCategories.stream()
                .map(category -> new CertificationCategoriesResponse(category.getId(), category.getName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CertificationCategoriesResponse> getCategories(final boolean isDefault){
        List<Category> categories;
        if (isDefault) {
            categories = categoryRepository.findByIsDefaultTrue();
        } else {
            categories = categoryRepository.findByIsDefaultFalseAndParentCategoryIsNull();
        }

        return categories.stream()
                .map(category -> new CertificationCategoriesResponse(category.getId(), category.getName()))
                .toList();
    }

    @Transactional(propagation=Propagation.REQUIRED, readOnly = true)
    public Long getRecommendedCategoryId(final Long memberId){
        if (memberId == null) return categoryQueryRepository.getDefaultRecommendedCategoryId();

        List<Long> interestedCategoryIds = categoryQueryRepository.findInterestedCategoryIds(memberId);
        if (interestedCategoryIds.isEmpty()) return categoryQueryRepository.getDefaultRecommendedCategoryId();
        if (interestedCategoryIds.size() == 1) return interestedCategoryIds.getFirst();

        Long randomItem = randomGenerator.getRandomItem(interestedCategoryIds);
        if (randomItem == null) return categoryQueryRepository.getDefaultRecommendedCategoryId();
        return randomItem;
    }

    @Transactional(readOnly = true)
    public List<CertificationsByCategoryResponse> getRecommendedCertifications(final Long memberId){
        long categoryId = getRecommendedCategoryId(memberId);

        long defaultSubCategoryId = categoryQueryRepository.getDefaultSubCategoryId(categoryId)
                .orElseThrow(SubCategoryNotFoundException::new);

        return certificationQueryRepository.findAllCertificationByCategory(defaultSubCategoryId,6);
    }
}
