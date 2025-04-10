package quartet.server.api.certification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import quartet.server.api.certification.dto.response.*;
import quartet.server.api.certification.query.CategoryQueryRepository;
import quartet.server.api.certification.query.CertificationQueryRepository;
import quartet.server.core.utils.RandomGenerator;
import quartet.server.domain.category.model.MainCategory;
import quartet.server.domain.category.model.SubCategory;
import quartet.server.domain.category.repository.MainCategoryRepository;
import quartet.server.domain.category.repository.SubCategoryRepository;
import quartet.server.domain.certification.repository.*;
import quartet.server.domain.certification.exception.CertificationNotFoundException;
import quartet.server.domain.category.exception.CategoryNotFoundException;
import quartet.server.domain.category.exception.SubCategoryNotFoundException;
import quartet.server.domain.member.repository.MemberCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificationService {
    private final MemberCategoryRepository memberCategoryRepository;
    private final AuthorityRepository authorityRepository;
    private final CertificationRepository certificationRepository;
    private final CertificationExamDetailRepository certificationExamRepository;
    private final CertificationPassCriteriaRepository certificationPassCriteriaRepository;
    private final CertificationScheduleRepository certificationScheduleRepository;
    private final CertificationViewLogRepository certificationViewLogRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    private final CategoryQueryRepository categoryQueryRepository;
    private final CertificationQueryRepository certificationQueryRepository;

    private final RandomGenerator randomGenerator;


    public CertificationResponse getCertification(final long certificationId) {
        return certificationQueryRepository.getCertification(certificationId)
                 .orElseThrow(CertificationNotFoundException::new);
    }

    public Page<CertificationSearchResponse> getAllCertificationsByCategory(
            long categoryId, final Pageable pageable) {
        SubCategory subCategory = subCategoryRepository.findById(categoryId)
                .orElseThrow(SubCategoryNotFoundException::new);

        return certificationQueryRepository.findAllCertificationByCategory(categoryId, pageable);
    }

    public List<CertificationCategoriesResponse> getCategories(final boolean isDefault) {
        List<MainCategory> mainCategories;
        if (isDefault) {
            mainCategories = mainCategoryRepository.findByIsDefaultTrue();
        } else {
            mainCategories = mainCategoryRepository.findByIsDefaultFalse();
        }

        return mainCategories.stream()
                .map(category -> new CertificationCategoriesResponse(
                    category.getId(), 
                    category.getName(),
                    CertificationCategoriesResponse.CategoryType.MAIN
                ))
                .toList();
    }

    public List<CertificationCategoriesResponse> getCategories(final long mainCategoryId) {
        if (!mainCategoryRepository.existsById(mainCategoryId)) {
            throw new CategoryNotFoundException();
        }
        
        List<SubCategory> subCategories = subCategoryRepository.findByMainCategoryId(mainCategoryId);
        
        return subCategories.stream()
                .map(category -> new CertificationCategoriesResponse(
                    category.getId(), 
                    category.getName(),
                    CertificationCategoriesResponse.CategoryType.SUB
                ))
                .toList();
    }

    public Long getRecommendedCategoryId(final Long memberId) {
        if (memberId == null) return categoryQueryRepository.getDefaultRecommendedCategoryId();

        List<Long> interestedCategoryIds = categoryQueryRepository.findInterestedCategoryIds(memberId);
        if (interestedCategoryIds.isEmpty()) return categoryQueryRepository.getDefaultRecommendedCategoryId();
        if (interestedCategoryIds.size() == 1) return interestedCategoryIds.getFirst();

        Long randomItem = randomGenerator.getRandomItem(interestedCategoryIds);
        if (randomItem == null) return categoryQueryRepository.getDefaultRecommendedCategoryId();
        return randomItem;
    }

    public List<CertificationSearchResponse> getRecommendedCertifications(final Long memberId) {
        long categoryId = getRecommendedCategoryId(memberId);
        return certificationQueryRepository.findAllCertificationByCategory(categoryId, 6);
    }

    public List<CertificationSearchResponse> getCertificationsBySearch(final String name) {
        return certificationQueryRepository.getCertificationsBySearch(name);
    }
}
