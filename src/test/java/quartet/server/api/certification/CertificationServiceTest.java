package quartet.server.api.certification;

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
import quartet.server.api.certification.query.CertificationQueryRepository;
import quartet.server.api.certification.query.CategoryQueryRepository;
import quartet.server.api.certification.service.CertificationService;
import quartet.server.core.utils.RandomGenerator;
import quartet.server.domain.category.exception.CategoryNotFoundException;
import quartet.server.domain.category.exception.SubCategoryNotFoundException;
import quartet.server.domain.category.model.Category;
import quartet.server.domain.category.repository.CategoryRepository;
import quartet.server.domain.certification.exception.CertificationNotFoundException;
import quartet.server.utils.fixture.Certification.CertificationCategoryFixture;
import quartet.server.utils.fixture.Certification.CertificationFixture;
import quartet.server.utils.fixture.Pageable.PageableFixture;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;



@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CertificationServiceTest {
    @Spy
    @InjectMocks
    private CertificationService certificationService;

    // 필요한 Repository mock 선언 (테스트 대상에서 실제 사용되는 리포지토리만 mock)
    @Mock
    private  CategoryRepository categoryRepository;

    @Mock
    private  CertificationQueryRepository certificationQueryRepository;

    @Mock
    private CategoryQueryRepository categoryQueryRepository;

    @Mock
    private RandomGenerator randomGenerator;

    @Nested
    class getCategories{
        @Test
        @DisplayName("메인페이지에 기본으로 나타나는 상위 자격증 카테고리를 조회한다")
        void success_withDefault(){
            // given
            boolean isDefault = true;
            Category category1 = Category.of("정보통신", null);
            Category category2 = Category.of("데이터분석", null);

            ReflectionTestUtils.setField(category1, "id", 1L);
            ReflectionTestUtils.setField(category2, "id", 2L);

            List<Category> categoryList =  List.of(category1, category2);
            List<CertificationCategoriesResponse> categoryResList = List.of(
                    new CertificationCategoriesResponse(category1.getId(),category1.getName()),
                    new CertificationCategoriesResponse(category2.getId(), category2.getName())
            );

            when(categoryRepository.findByIsDefaultTrue()).thenReturn(categoryList);

            // when
            List<CertificationCategoriesResponse> result = certificationService.getCategories(isDefault);

            // then
            assertThat(result).hasSize(categoryList.size());
            assertThat(result).isEqualTo(categoryResList);
            verify(categoryRepository, times(1)).findByIsDefaultTrue();
            verify(categoryRepository, never()).findByIsDefaultFalseAndParentCategoryIsNull();
            verify(categoryRepository, never()).findByParentCategory_Id(anyLong());
        }

        @Test
        @DisplayName("메인페이지에 더보기 클릭시 나타나는 기타 상위 자격증 카테고리를 조회한다")
        void success_withExtra(){
            // given
            boolean isDefault = false;
            Category category1 = Category.of("건축", null);
            Category category2 = Category.of("우주항공", null);

            ReflectionTestUtils.setField(category1, "id", 1L);
            ReflectionTestUtils.setField(category2, "id", 2L);

            List<Category> categoryList =  List.of(category1, category2);
            List<CertificationCategoriesResponse> categoryResList = List.of(
                    new CertificationCategoriesResponse(category1.getId(),category1.getName()),
                    new CertificationCategoriesResponse(category2.getId(), category2.getName())
            );

            when(categoryRepository.findByIsDefaultFalseAndParentCategoryIsNull()).thenReturn(categoryList);

            // when
            List<CertificationCategoriesResponse> result = certificationService.getCategories(isDefault);

            // then
            assertThat(result).hasSize(categoryList.size());
            assertThat(result).isEqualTo(categoryResList);
            verify(categoryRepository, never()).findByIsDefaultTrue();
            verify(categoryRepository, times(1)).findByIsDefaultFalseAndParentCategoryIsNull();
            verify(categoryRepository, never()).findByParentCategory_Id(anyLong());
        }

        @Test
        @DisplayName("특정 상위 카테고리에 속하는 하위 자격증 카테고리를 조회한다")
        void success_byParentCategory(){
            // given
            long parentId = 1L;
            Category parentCategory = Category.of("IT", null);
            Category category1 = Category.of("빅데이터", parentCategory);
            Category category2 = Category.of("데이터베이스", parentCategory);

            ReflectionTestUtils.setField(parentCategory, "id", 1L);
            ReflectionTestUtils.setField(category1, "id", 2L);
            ReflectionTestUtils.setField(category2, "id", 3L);

            List<Category> categoryList =  List.of(category1, category2);
            List<CertificationCategoriesResponse> categoryResList = List.of(
                    new CertificationCategoriesResponse(category1.getId(),category1.getName()),
                    new CertificationCategoriesResponse(category2.getId(), category2.getName())
            );

            when(categoryRepository.findByParentCategory_Id(parentId)).thenReturn(categoryList);

            // when
            List<CertificationCategoriesResponse> result = certificationService.getCategories(parentId);

            // then
            assertThat(result).hasSize(categoryList.size());
            assertThat(result).isEqualTo(categoryResList);
            verify(categoryRepository, never()).findByIsDefaultTrue();
            verify(categoryRepository, never()).findByIsDefaultFalseAndParentCategoryIsNull();
            verify(categoryRepository, times(1)).findByParentCategory_Id(anyLong());
        }

    }


    @Nested
    class getAllCertificationByCategory{
        
        @Test
        @DisplayName("특정 자격증 카테고리에 속하는 자격증들을 조회한다. 이때, 상위 카테고리 id가 제공되면, 디폴트 하위에 대한 자격증을 조회한다")
        void success_withParentCategoryId(){
            // given
            long categoryId = 1L;
            long subCategoryId = 2L;
            Pageable pageable = PageableFixture.pageable();
            Category category = CertificationCategoryFixture.parentCategory();
            List<CertificationsByCategoryResponse> certificationList = CertificationFixture.certificationsByCategoryRes();
            Page<CertificationsByCategoryResponse> certificationPage = new PageImpl<>(certificationList);

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(categoryQueryRepository.getDefaultSubCategoryId(categoryId)).thenReturn(Optional.of(subCategoryId));
            when(certificationQueryRepository.findAllCertificationByCategory(subCategoryId,pageable))
                    .thenReturn(certificationPage);

            // when
            Page<CertificationsByCategoryResponse> result = certificationService.getAllCertificationsByCategory(
                    categoryId, pageable);

            // then
            assertThat(result).isEqualTo(certificationPage);
            verify(categoryQueryRepository, times(1)).getDefaultSubCategoryId(anyLong());
            verify(certificationQueryRepository, never()).findAllCertificationByCategory(categoryId, pageable);
            verify(certificationQueryRepository, times(1)).findAllCertificationByCategory(subCategoryId, pageable);
        }
        
        @Test
        @DisplayName("특정 자격증 카테고리에 속하는 자격증들을 조회한다. 이때, 하위 카테고리 id가 제공되면, 해당 하위에 대한 자격증을 조회한다")
        void success_withSubCategoryId(){
            // given
            long categoryId = 1L;
            Pageable pageable = PageableFixture.pageable();
            Category category = CertificationCategoryFixture.subCategory();
            List<CertificationsByCategoryResponse> certificationList = CertificationFixture.certificationsByCategoryRes();
            Page<CertificationsByCategoryResponse> certificationPage = new PageImpl<>(certificationList);

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(certificationQueryRepository.findAllCertificationByCategory(categoryId,pageable))
                    .thenReturn(certificationPage);

            // when
            Page<CertificationsByCategoryResponse> result = certificationService.getAllCertificationsByCategory(
                    categoryId, pageable);

            // then
            assertThat(result).isEqualTo(certificationPage);
            verify(categoryQueryRepository, never()).getDefaultSubCategoryId(anyLong());
            verify(certificationQueryRepository, times(1)).findAllCertificationByCategory(categoryId, pageable);
        }

        @Test
        @DisplayName("존재하지 않는 자격증 카테고리 id가 제공되면 예외를 반환한다")
        void fail_notFoundCategoryException(){
            // given
            long categoryId = 1L;
            Pageable pageable = PageableFixture.pageable();
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(CategoryNotFoundException.class, () -> {certificationService.getAllCertificationsByCategory(categoryId, pageable);});
            verify(categoryQueryRepository, never()).getDefaultSubCategoryId(anyLong());
            verify(certificationQueryRepository, never()).findAllCertificationByCategory(categoryId, pageable);
        }

        @Test
        @DisplayName("상위 자격증 카테고리에 대한 하위 카테고리가 없으면, 예외를 반환한다")
        void fail_notFoundSubCategoryException(){
            // given
            long categoryId = 1L;
            Pageable pageable = PageableFixture.pageable();
            Category category = CertificationCategoryFixture.parentCategory();
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(categoryQueryRepository.getDefaultSubCategoryId(categoryId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(SubCategoryNotFoundException.class, () -> {certificationService.getAllCertificationsByCategory(categoryId, pageable);});
            verify(categoryQueryRepository, times(1)).getDefaultSubCategoryId(anyLong());
            verify(certificationQueryRepository, never()).findAllCertificationByCategory(categoryId, pageable);
        }
    }


    @Nested
    class getCertification{
        @Test
        @DisplayName("자격증에 대한 상세 정보를 조회한다")
        public void success(){
            // given
            long certificationId = 1L;
            CertificationResponse certificationResponse = CertificationFixture.certificationRes(certificationId);
            when(certificationQueryRepository.getCertification(certificationId)).thenReturn(Optional.of(certificationResponse));

            // when
            CertificationResponse result = certificationService.getCertification(certificationId);

            // then
            assertThat(result).isEqualTo(certificationResponse);
            verify(certificationQueryRepository, times(1)).getCertification(certificationId);

        }

        @Test
        @DisplayName("존재하지 않는 자격증이면, 예외를 반환한다")
        public void fail_notFoundCertification(){
            // given
            long certificationId = 99L;
            when(certificationQueryRepository.getCertification(certificationId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(CertificationNotFoundException.class, () -> {certificationService.getCertification(certificationId);});
            verify(certificationQueryRepository, times(1)).getCertification(certificationId);
        }
    }

    @Nested
    class getRecommendedCategoryId{
        @Test
        @DisplayName("비로그인 유저일때 기본 추천 자격증을 반환한다")
        public void success_whenMemberIdIsNull(){
            // given
            final long defaultCategoryId = 1L;
            when(categoryQueryRepository.getDefaultRecommendedCategoryId()).thenReturn(defaultCategoryId);

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
            when(categoryQueryRepository.findInterestedCategoryIds(memberId)).thenReturn(Collections.emptyList());
            when(categoryQueryRepository.getDefaultRecommendedCategoryId()).thenReturn(defaultCategoryId);

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
            when(categoryQueryRepository.findInterestedCategoryIds(memberId)).thenReturn(List.of(categoryId));

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
            final long randomCategoryId = 20L; // 랜덤으로 선택될 값

            when(categoryQueryRepository.findInterestedCategoryIds(memberId)).thenReturn(interestedCategoryIds);
            when(randomGenerator.getRandomItem(interestedCategoryIds)).thenReturn(randomCategoryId);

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
    class getRecommendedCertifications{

        @Test
        @DisplayName("관심 카테고리가 있는 로그인 유저에게, 관심 분야에 대한 추천 자격증을 조회한다")
        public void success(){
            // given
            final long memberId = 1L;
            final long randomCategoryId = 1L;
            final long defaultSubCategoryId = 3L;
            final List<CertificationsByCategoryResponse> certificationList = CertificationFixture.certificationsByCategoryRes();
            doReturn(randomCategoryId).when(certificationService).getRecommendedCategoryId(memberId);
            when(categoryQueryRepository.getDefaultSubCategoryId(randomCategoryId)).thenReturn(Optional.of(defaultSubCategoryId));
            when(certificationQueryRepository.findAllCertificationByCategory(defaultSubCategoryId,6)).thenReturn(certificationList);

            // when
            final List<CertificationsByCategoryResponse> result = certificationService.getRecommendedCertifications(memberId);

            // then
            assertThat(result).isEqualTo(certificationList);
            verify(certificationService, times(1)).getRecommendedCategoryId(memberId);
            verify(categoryQueryRepository, times(1)).getDefaultSubCategoryId(randomCategoryId);
            verify(certificationQueryRepository,times(1)).findAllCertificationByCategory(defaultSubCategoryId, 6);
        }

        @Test
        @DisplayName("하위 카테고리 id에 대한 카테고리가 없을때 예외를 반환한다")
        public void fail_notFoundSubCategoryException() {
            // given
            final long memberId = 1L;
            final long categoryId = 1L;
            final long subCategoryId = 2L;
            doReturn(categoryId).when(certificationService).getRecommendedCategoryId(memberId);
            when(categoryQueryRepository.getDefaultSubCategoryId(categoryId)).thenReturn(Optional.empty());

            // when & then (예외 발생 검증)
            assertThatThrownBy(() -> certificationService.getRecommendedCertifications(memberId))
                    .isInstanceOf(SubCategoryNotFoundException.class);
            verify(certificationService, times(1)).getRecommendedCategoryId(memberId);
            verify(categoryQueryRepository, times(1)).getDefaultSubCategoryId(categoryId);
            verify(certificationQueryRepository, never()).findAllCertificationByCategory(anyLong(), anyInt());
        }
    }

}