package quartet.server.api.certification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import quartet.server.api.certification.dto.response.CertificationCategoriesResponse;
import quartet.server.api.certification.dto.response.CertificationResponse;
import quartet.server.api.certification.dto.response.CertificationsByCategoryResponse;
import quartet.server.api.certification.dto.response.CertificationSearchResponse;
import quartet.server.api.certification.query.CategoryQueryRepository;
import quartet.server.api.certification.query.CertificationQueryRepository;
import quartet.server.core.utils.RandomGenerator;
import quartet.server.domain.category.model.MainCategory;
import quartet.server.domain.category.model.SubCategory;
import quartet.server.domain.category.repository.MainCategoryRepository;
import quartet.server.domain.category.repository.SubCategoryRepository;
import quartet.server.domain.certification.repository.*;
import quartet.server.domain.category.exception.SubCategoryNotFoundException;
import quartet.server.domain.certification.exception.CertificationNotFoundException;
import quartet.server.domain.member.repository.MemberCategoryRepository;
import quartet.server.utils.fixture.Certification.CertificationCategoryFixture;
import quartet.server.utils.fixture.Certification.CertificationFixture;
import quartet.server.utils.fixture.Pageable.PageableFixture;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CertificationServiceTest {
    @Spy
    @InjectMocks
    private CertificationService certificationService;

    @Mock
    private MemberCategoryRepository memberCategoryRepository;
    @Mock
    private AuthorityRepository authorityRepository;
    @Mock
    private CertificationRepository certificationRepository;
    @Mock
    private CertificationExamDetailRepository certificationExamRepository;
    @Mock
    private CertificationPassCriteriaRepository certificationPassCriteriaRepository;
    @Mock
    private CertificationScheduleRepository certificationScheduleRepository;
    @Mock
    private CertificationViewLogRepository certificationViewLogRepository;
    @Mock
    private MainCategoryRepository mainCategoryRepository;
    @Mock
    private SubCategoryRepository subCategoryRepository;

    @Mock
    private CategoryQueryRepository categoryQueryRepository;
    @Mock
    private CertificationQueryRepository certificationQueryRepository;

    @Mock
    private RandomGenerator randomGenerator;

    private MainCategory mainCategory;
    private SubCategory subCategory;

    @BeforeEach
    void setUp() {
        mainCategory = CertificationCategoryFixture.mainCategory();
        subCategory = CertificationCategoryFixture.subCategory(mainCategory);
    }

    @Nested
    class getCategories {
        @Test
        @DisplayName("메인페이지에 기본으로 나타나는 대분류 자격증 카테고리를 조회한다")
        void success_withDefault() {
            // given
            boolean isDefault = true;
            MainCategory mainCategory1 = MainCategory.of("IT", true);
            MainCategory mainCategory2 = MainCategory.of("건축", true);

            ReflectionTestUtils.setField(mainCategory1, "id", 1L);
            ReflectionTestUtils.setField(mainCategory2, "id", 2L);

            List<MainCategory> mainCategoryList = List.of(mainCategory1, mainCategory2);
            List<CertificationCategoriesResponse> categoryResList = List.of(
                    new CertificationCategoriesResponse(mainCategory1.getId(), mainCategory1.getName(), CertificationCategoriesResponse.CategoryType.MAIN),
                    new CertificationCategoriesResponse(mainCategory2.getId(), mainCategory2.getName(), CertificationCategoriesResponse.CategoryType.MAIN)
            );

            when(mainCategoryRepository.findByIsDefaultTrue()).thenReturn(mainCategoryList);

            // when
            List<CertificationCategoriesResponse> result = certificationService.getCategories(isDefault);

            // then
            assertThat(result).hasSize(mainCategoryList.size());
            assertThat(result).isEqualTo(categoryResList);
            verify(mainCategoryRepository, times(1)).findByIsDefaultTrue();
            verify(mainCategoryRepository, never()).findByIsDefaultFalse();
        }

        @Test
        @DisplayName("메인페이지에 더보기 클릭시 나타나는 기타 대분류 자격증 카테고리를 조회한다")
        void success_withExtra() {
            // given
            boolean isDefault = false;
            MainCategory mainCategory1 = MainCategory.of("우주항공", false);
            MainCategory mainCategory2 = MainCategory.of("해양", false);

            ReflectionTestUtils.setField(mainCategory1, "id", 1L);
            ReflectionTestUtils.setField(mainCategory2, "id", 2L);

            List<MainCategory> mainCategoryList = List.of(mainCategory1, mainCategory2);
            List<CertificationCategoriesResponse> categoryResList = List.of(
                    new CertificationCategoriesResponse(mainCategory1.getId(), mainCategory1.getName(), CertificationCategoriesResponse.CategoryType.MAIN),
                    new CertificationCategoriesResponse(mainCategory2.getId(), mainCategory2.getName(), CertificationCategoriesResponse.CategoryType.MAIN)
            );

            when(mainCategoryRepository.findByIsDefaultFalse()).thenReturn(mainCategoryList);

            // when
            List<CertificationCategoriesResponse> result = certificationService.getCategories(isDefault);

            // then
            assertThat(result).hasSize(mainCategoryList.size());
            assertThat(result).isEqualTo(categoryResList);
            verify(mainCategoryRepository, never()).findByIsDefaultTrue();
            verify(mainCategoryRepository, times(1)).findByIsDefaultFalse();
        }

        @Test
        @DisplayName("특정 대분류 카테고리에 속하는 소분류 자격증 카테고리를 조회한다")
        void success_byMainCategory() {
            // given
            long mainCategoryId = 1L;
            MainCategory mainCategory = MainCategory.of("IT", true);
            SubCategory subCategory1 = SubCategory.of("빅데이터", mainCategory);
            SubCategory subCategory2 = SubCategory.of("데이터베이스", mainCategory);

            ReflectionTestUtils.setField(mainCategory, "id", 1L);
            ReflectionTestUtils.setField(subCategory1, "id", 2L);
            ReflectionTestUtils.setField(subCategory2, "id", 3L);

            List<SubCategory> subCategoryList = List.of(subCategory1, subCategory2);
            List<CertificationCategoriesResponse> categoryResList = List.of(
                    new CertificationCategoriesResponse(subCategory1.getId(), subCategory1.getName(), CertificationCategoriesResponse.CategoryType.SUB),
                    new CertificationCategoriesResponse(subCategory2.getId(), subCategory2.getName(), CertificationCategoriesResponse.CategoryType.SUB)
            );

            when(subCategoryRepository.findByMainCategoryId(mainCategoryId)).thenReturn(subCategoryList);

            // when
            List<CertificationCategoriesResponse> result = certificationService.getCategories(mainCategoryId);

            // then
            assertThat(result).hasSize(subCategoryList.size());
            assertThat(result).isEqualTo(categoryResList);
            verify(subCategoryRepository, times(1)).findByMainCategoryId(mainCategoryId);
        }
    }

    @Nested
    class getAllCertificationByCategory {
        @Test
        @DisplayName("특정 소분류 자격증 카테고리에 속하는 자격증들을 조회한다")
        void success() {
            // given
            long subCategoryId = 1L;
            Pageable pageable = PageableFixture.pageable();
            SubCategory subCategory = CertificationCategoryFixture.subCategory(CertificationCategoryFixture.mainCategory());
            List<CertificationSearchResponse> certificationList = List.of(
                new CertificationSearchResponse(1L, "IT", "데이터베이스", "자격증 1", Instant.parse("2025-05-01T09:00:00Z"), Instant.parse("2025-05-01T10:00:00Z"), 100),
                new CertificationSearchResponse(2L, "IT", "프로그래밍", "자격증 2", Instant.parse("2025-05-01T08:00:00Z"), Instant.parse("2025-05-01T10:00:00Z"), 50)
            );
            Page<CertificationSearchResponse> certificationPage = new PageImpl<>(certificationList);

            when(subCategoryRepository.findById(subCategoryId)).thenReturn(Optional.of(subCategory));
            when(certificationQueryRepository.findAllCertificationByCategory(subCategoryId, pageable))
                    .thenReturn(certificationPage);

            // when
            Page<CertificationSearchResponse> result = certificationService.getAllCertificationsByCategory(
                    subCategoryId, pageable);

            // then
            assertThat(result).isEqualTo(certificationPage);
            verify(certificationQueryRepository, times(1)).findAllCertificationByCategory(subCategoryId, pageable);
        }

        @Test
        @DisplayName("존재하지 않는 소분류 자격증 카테고리 id가 제공되면 예외를 반환한다")
        void fail_notFoundSubCategoryException() {
            // given
            long subCategoryId = 1L;
            Pageable pageable = PageableFixture.pageable();
            when(subCategoryRepository.findById(subCategoryId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> certificationService.getAllCertificationsByCategory(subCategoryId, pageable))
                    .isInstanceOf(SubCategoryNotFoundException.class);
            verify(certificationQueryRepository, never()).findAllCertificationByCategory(subCategoryId, pageable);
        }
    }

    @Nested
    class getCertification {
        @Test
        @DisplayName("자격증에 대한 상세 정보를 조회한다")
        public void success() {
            // given
            long certificationId = 1L;
            CertificationResponse certificationResponse = CertificationFixture.certificationResponse();
            when(certificationQueryRepository.getCertification(certificationId))
                    .thenReturn(Optional.of(certificationResponse));

            // when
            CertificationResponse result = certificationService.getCertification(certificationId);

            // then
            assertThat(result).isEqualTo(certificationResponse);
            verify(certificationQueryRepository, times(1)).getCertification(certificationId);
        }

        @Test
        @DisplayName("존재하지 않는 자격증이면, 예외를 반환한다")
        public void fail_notFoundCertification() {
            // given
            long certificationId = 99L;
            when(certificationQueryRepository.getCertification(certificationId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> certificationService.getCertification(certificationId))
                    .isInstanceOf(CertificationNotFoundException.class);
            verify(certificationQueryRepository, times(1)).getCertification(certificationId);
        }
    }

    @Nested
    class getRecommendedCategoryId {
        @Test
        @DisplayName("비로그인 유저일때 기본 추천 자격증을 반환한다")
        public void success_whenMemberIdIsNull() {
            // given
            final long defaultCategoryId = 1L;
            when(categoryQueryRepository.getDefaultRecommendedCategoryId())
                    .thenReturn(defaultCategoryId);

            // when
            final Long result = certificationService.getRecommendedCategoryId(null);

            // then
            assertThat(result).isEqualTo(defaultCategoryId);
            verify(categoryQueryRepository, times(1)).getDefaultRecommendedCategoryId();
            verify(categoryQueryRepository, never()).findInterestedCategoryIds(anyLong());
            verify(randomGenerator, never()).getRandomItem(anyList());
        }

        @Test
        @DisplayName("로그인 유저의 관심 카테고리 목록이 비어 있으면 기본 추천 자격증을 반환한다")
        public void success_whenMemberWithoutInterestedCategories() {
            // given
            final long memberId = 1L;
            final long defaultCategoryId = 1L;
            when(categoryQueryRepository.findInterestedCategoryIds(memberId))
                    .thenReturn(Collections.emptyList());
            when(categoryQueryRepository.getDefaultRecommendedCategoryId())
                    .thenReturn(defaultCategoryId);

            // when
            final long result = certificationService.getRecommendedCategoryId(memberId);

            // then
            assertThat(result).isEqualTo(defaultCategoryId);
            verify(categoryQueryRepository, times(1)).findInterestedCategoryIds(memberId);
            verify(categoryQueryRepository, times(1)).getDefaultRecommendedCategoryId();
            verify(randomGenerator, never()).getRandomItem(anyList());
        }

        @Test
        @DisplayName("로그인 유저의 관심 카테고리가 1개만 있으면 해당 카테고리의 추천 자격증을 반환한다")
        public void success_whenMemberHasSingleInterestedCategory() {
            // given
            final long memberId = 1L;
            final long categoryId = 1L;
            when(categoryQueryRepository.findInterestedCategoryIds(memberId))
                    .thenReturn(List.of(categoryId));

            // when
            final long result = certificationService.getRecommendedCategoryId(memberId);

            // then
            assertThat(result).isEqualTo(categoryId);
            verify(categoryQueryRepository, times(1)).findInterestedCategoryIds(memberId);
            verify(categoryQueryRepository, never()).getDefaultRecommendedCategoryId();
            verify(randomGenerator, never()).getRandomItem(anyList());
        }

        @Test
        @DisplayName("로그인 유저의 관심 카테고리가 여러 개 있으면 랜덤으로 선택된 카테고리를 반환한다")
        public void success_whenMemberHasMultipleInterestedCategory() {
            // given
            final long memberId = 1L;
            final List<Long> interestedCategoryIds = List.of(10L, 20L, 30L);
            final long randomCategoryId = 20L;

            when(categoryQueryRepository.findInterestedCategoryIds(memberId))
                    .thenReturn(interestedCategoryIds);
            when(randomGenerator.getRandomItem(interestedCategoryIds))
                    .thenReturn(randomCategoryId);

            // when
            final long result = certificationService.getRecommendedCategoryId(memberId);

            // then
            assertThat(result).isEqualTo(randomCategoryId);
            verify(categoryQueryRepository, times(1)).findInterestedCategoryIds(memberId);
            verify(randomGenerator, times(1)).getRandomItem(interestedCategoryIds);
            verify(categoryQueryRepository, never()).getDefaultRecommendedCategoryId();
        }
    }

    @Nested
    class getRecommendedCertifications {
        @Test
        @DisplayName("관심 카테고리가 있는 로그인 유저에게, 관심 분야에 대한 추천 자격증을 조회한다")
        public void success() {
            // given
            final long memberId = 1L;
            final long categoryId = 1L;
            final List<CertificationSearchResponse> certificationList = List.of(
                new CertificationSearchResponse(1L, "IT", "데이터베이스", "자격증 1", Instant.parse("2025-05-01T09:00:00Z"), Instant.parse("2025-05-01T10:00:00Z"), 100),
                new CertificationSearchResponse(2L, "IT", "프로그래밍", "자격증 2", Instant.parse("2025-05-01T08:00:00Z"), Instant.parse("2025-05-01T10:00:00Z"), 50)
            );
            doReturn(categoryId).when(certificationService).getRecommendedCategoryId(memberId);
            when(certificationQueryRepository.findAllCertificationByCategory(categoryId, 6))
                    .thenReturn(certificationList);

            // when
            final List<CertificationSearchResponse> result = certificationService.getRecommendedCertifications(memberId);

            // then
            assertThat(result).isEqualTo(certificationList);
            verify(certificationService, times(1)).getRecommendedCategoryId(memberId);
            verify(certificationQueryRepository, times(1)).findAllCertificationByCategory(categoryId, 6);
        }
    }
} 