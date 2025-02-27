package quartet.server.api.certification;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import quartet.server.api.certification.dto.response.CertificationCategoriesRes;
import quartet.server.api.certification.dto.response.CertificationRes;
import quartet.server.api.certification.dto.response.CertificationsByCategoryRes;
import quartet.server.api.certification.query.CertificationQueryRepository;
import quartet.server.domain.category.model.Category;
import quartet.server.domain.category.repository.CategoryRepository;
import quartet.server.utils.fixture.CertificationFixture;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;



@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CertificationServiceTest {
    @InjectMocks
    private CertificationService certificationService;

    // 필요한 Repository mock 선언 (테스트 대상에서 실제 사용되는 리포지토리만 mock)
    @Mock
    private  CategoryRepository categoryRepository;

    @Mock
    private  CertificationQueryRepository certificationQueryRepository;

    @Nested
    class getCategories{
        @Test
        void success_withDefault(){
            // given
            boolean isDefault = true;
            Category category1 = Category.of("정보통신", null);
            Category category2 = Category.of("데이터분석", null);

            ReflectionTestUtils.setField(category1, "id", 1L);
            ReflectionTestUtils.setField(category2, "id", 2L);

            List<Category> categoryList =  List.of(category1, category2);
            List<CertificationCategoriesRes> categoryResList = List.of(
                    new CertificationCategoriesRes(category1.getId(),category1.getName()),
                    new CertificationCategoriesRes(category2.getId(), category2.getName())
            );

            when(categoryRepository.findByIsDefaultTrue()).thenReturn(categoryList);

            // when
            List<CertificationCategoriesRes> result = certificationService.getCategories(isDefault);

            // then
            assertThat(result).hasSize(categoryList.size());
            assertThat(result).isEqualTo(categoryResList);
            verify(categoryRepository, times(1)).findByIsDefaultTrue();
            verify(categoryRepository, never()).findByIsDefaultFalseAndParentCategoryIsNull();
            verify(categoryRepository, never()).findByParentCategory_Id(anyLong());
        }

        @Test
        void success_withExtra(){
            // given
            boolean isDefault = false;
            Category category1 = Category.of("건축", null);
            Category category2 = Category.of("우주항공", null);

            ReflectionTestUtils.setField(category1, "id", 1L);
            ReflectionTestUtils.setField(category2, "id", 2L);

            List<Category> categoryList =  List.of(category1, category2);
            List<CertificationCategoriesRes> categoryResList = List.of(
                    new CertificationCategoriesRes(category1.getId(),category1.getName()),
                    new CertificationCategoriesRes(category2.getId(), category2.getName())
            );

            when(categoryRepository.findByIsDefaultFalseAndParentCategoryIsNull()).thenReturn(categoryList);

            // when
            List<CertificationCategoriesRes> result = certificationService.getCategories(isDefault);

            // then
            assertThat(result).hasSize(categoryList.size());
            assertThat(result).isEqualTo(categoryResList);
            verify(categoryRepository, never()).findByIsDefaultTrue();
            verify(categoryRepository, times(1)).findByIsDefaultFalseAndParentCategoryIsNull();
            verify(categoryRepository, never()).findByParentCategory_Id(anyLong());
        }

        @Test
        void success_withSub(){
            // given
            long parentId = 1L;
            Category parentCategory = Category.of("IT", null);
            Category category1 = Category.of("빅데이터", parentCategory);
            Category category2 = Category.of("데이터베이스", parentCategory);

            ReflectionTestUtils.setField(parentCategory, "id", 1L);
            ReflectionTestUtils.setField(category1, "id", 2L);
            ReflectionTestUtils.setField(category2, "id", 3L);

            List<Category> categoryList =  List.of(category1, category2);
            List<CertificationCategoriesRes> categoryResList = List.of(
                    new CertificationCategoriesRes(category1.getId(),category1.getName()),
                    new CertificationCategoriesRes(category2.getId(), category2.getName())
            );

            when(categoryRepository.findByParentCategory_Id(parentId)).thenReturn(categoryList);

            // when
            List<CertificationCategoriesRes> result = certificationService.getCategories(parentId);

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
        /* 특정 대분류 카테고리에 접속했을때, 디폴트로 보여지는 서브 카테고리에 대한 자격증 */
        @Test
        void success_withParentCategoryId(){
            // given
            boolean isSubCategory = false;
            long categoryId = 1L;
            long subCategoryId = 2L;
            Pageable pageable = PageRequest.of(0, 10);
            List<CertificationsByCategoryRes> certificationList = CertificationFixture.certificationsByCategoryRes();
            Page<CertificationsByCategoryRes> certificationPage = new PageImpl<>(certificationList);

            when(certificationQueryRepository.getDefaultSubCategoryId(categoryId)).thenReturn(subCategoryId);
            when(certificationQueryRepository.findAllCertificationByCategory(subCategoryId,pageable))
                    .thenReturn(certificationPage);

            // when
            Page<CertificationsByCategoryRes> result = certificationService.getAllCertificationsByCategory(
                    categoryId, isSubCategory, pageable);

            // then
            assertThat(result).isEqualTo(certificationPage);
            verify(certificationQueryRepository, times(1)).getDefaultSubCategoryId(anyLong());
            verify(certificationQueryRepository, never()).findAllCertificationByCategory(categoryId, pageable);
            verify(certificationQueryRepository, times(1)).findAllCertificationByCategory(subCategoryId, pageable);
        }

        /* 특정 서브 카테고리를 선택했을때 조회되는 자격증 */
        @Test
        void success_withSubCategoryId(){
            // given
            boolean isSubCategory = true;
            long categoryId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            List<CertificationsByCategoryRes> certificationList = CertificationFixture.certificationsByCategoryRes();
            Page<CertificationsByCategoryRes> certificationPage = new PageImpl<>(certificationList);

            when(certificationQueryRepository.findAllCertificationByCategory(categoryId,pageable))
                    .thenReturn(certificationPage);

            // when
            Page<CertificationsByCategoryRes> result = certificationService.getAllCertificationsByCategory(
                    categoryId, isSubCategory, pageable);

            // then
            assertThat(result).isEqualTo(certificationPage);
            verify(certificationQueryRepository, never()).getDefaultSubCategoryId(anyLong());
            verify(certificationQueryRepository, times(1)).findAllCertificationByCategory(categoryId, pageable);
        }
    }


    @Nested
    class getCertification{
        @Test
        public void success(){
            // given
            long certificationId = 1L;
            CertificationRes certificationRes = CertificationFixture.certificationRes(certificationId);

            when(certificationQueryRepository.getCertification(certificationId)).thenReturn(certificationRes);

            // when
            CertificationRes result = certificationService.getCertification(certificationId);

            // then
            assertThat(result).isEqualTo(certificationRes);
            verify(certificationQueryRepository, times(1)).getCertification(certificationId);

        }
    }


}
