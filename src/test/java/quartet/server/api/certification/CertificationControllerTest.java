package quartet.server.api.certification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import quartet.server.api.certification.dto.response.CertificationCategoriesRes;
import quartet.server.api.certification.dto.response.CertificationRes;
import quartet.server.api.certification.dto.response.CertificationsByCategoryRes;
import quartet.server.utils.fixture.Certification.CertificationFixture;
import quartet.server.utils.fixture.Certification.CertificationCategoryFixture;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CertificationController.class)
public class CertificationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CertificationService certificationService;

    @Test
    @DisplayName("메인페이지에 기본으로 나타나는 상위 자격증 카테고리를 조회한다")
    void success_getCategories_withDefault() throws Exception {
        // given
        boolean isDefault = true;
        List<CertificationCategoriesRes> categoryList = CertificationCategoryFixture.categoryResList();
        when(certificationService.getCategories(eq(isDefault))).thenReturn(categoryList);

        // when
        mockMvc.perform(
                get("/certification/category")
                        .param("isDefault", String.valueOf(isDefault))
                        .contentType(MediaType.APPLICATION_JSON)
        )
        // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data", hasSize(categoryList.size())));

        verify(certificationService, never()).getCategories(anyLong());
        verify(certificationService, times(1)).getCategories(isDefault);
    }

    @Test
    @DisplayName("메인페이지에 더보기 클릭시 나타나는 기타 상위 자격증 카테고리를 조회한다")
    void success_getCategories_withExtra() throws Exception {
        // given
        boolean isDefault = false;
        List<CertificationCategoriesRes> categoryList = CertificationCategoryFixture.categoryResList();

        when(certificationService.getCategories(eq(isDefault))).thenReturn(categoryList);

        // when
        mockMvc.perform(
                get("/certification/category")
                        .param("isDefault", String.valueOf(isDefault))
                        .contentType(MediaType.APPLICATION_JSON)
        )
        // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data", hasSize(categoryList.size())));

        verify(certificationService, never()).getCategories(anyLong());
        verify(certificationService, times(1)).getCategories(isDefault);
    }

    @Test
    @DisplayName("특정 상위 카테고리에 속하는 하위 자격증 카테고리를 조회한다")
    void success_getCategories_byParentCategory() throws Exception {
        // given
        long parentCategoryId = 1L;
        List<CertificationCategoriesRes> categoryList = CertificationCategoryFixture.categoryResList();

        when(certificationService.getCategories(eq(parentCategoryId))).thenReturn(categoryList);

        // when
        mockMvc.perform(
                get("/certification/category")
                        .param("parentId", String.valueOf(parentCategoryId))
                        .contentType(MediaType.APPLICATION_JSON)
        )
        // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data", hasSize(categoryList.size())));

        verify(certificationService, times(1)).getCategories(parentCategoryId);
        verify(certificationService, never()).getCategories(anyBoolean());
    }

    @Test
    @DisplayName("특정 자격증 카테고리에 속하는 자격증들을 조회한다")
    void success_getAllCertificationByCategoryTest() throws Exception {
        // given
        long categoryId = 1L;
        List<CertificationsByCategoryRes> certificationList = CertificationFixture.certificationsByCategoryRes();
        Page<CertificationsByCategoryRes> certificationPage = new PageImpl<>(certificationList);

        when(certificationService.getAllCertificationsByCategory(
                eq(categoryId),
                any(PageRequest.class)
        )).thenReturn(certificationPage);

        // when
        mockMvc.perform(
                get("/certification")
                        .param("categoryId", String.valueOf(categoryId))
                        .param("page", "0")
                        .param("pageSize", "2")
                        .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.content", hasSize(certificationList.size())));
    }

    @Test
    @DisplayName("자격증에 대한 상세 정보를 조회한다")
    void success_getCertification() throws Exception {
        // given
        long certificationId  = 1L;
        CertificationRes certificationRes = CertificationFixture.certificationRes(certificationId);
        when(certificationService.getCertification(eq(certificationId))).thenReturn(certificationRes);

        // when
        mockMvc.perform(
                get("/certification/{certificationId}",certificationId)
                       .contentType(MediaType.APPLICATION_JSON)
        )

        // then
         .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.id").value(certificationId));
    }
}
