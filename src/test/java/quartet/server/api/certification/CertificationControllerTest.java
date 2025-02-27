package quartet.server.api.certification;

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
import quartet.server.utils.fixture.CertificationFixture;
import quartet.server.utils.fixture.CertificationCategoryFixture;
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

    /*메인 페이지에 디폴트로 나오는 대분류 카테고리 */
    @Test
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

    /*메인 페이지에 '기타' 항목에 나오는 대분류 카테고리 */
    @Test
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
    void success_getCategories_withSub() throws Exception {
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
    void success_getAllCertificationByCategoryTest() throws Exception {
        // given
        long categoryId = 1L;
        List<CertificationsByCategoryRes> certificationList = CertificationFixture.certificationsByCategoryRes();
        Page<CertificationsByCategoryRes> certificationPage = new PageImpl<>(certificationList);

        when(certificationService.getAllCertificationsByCategory(
                eq(categoryId),
                eq(true),
                any(PageRequest.class)
        )).thenReturn(certificationPage);

        // when
        mockMvc.perform(
                get("/certification")
                        // RequestParam 설정
                        .param("categoryId", String.valueOf(categoryId))
                        .param("isSubCategory", "true")
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
