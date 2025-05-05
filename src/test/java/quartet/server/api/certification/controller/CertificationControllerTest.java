package quartet.server.api.certification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import quartet.server.api.certification.dto.response.CertificationCategoriesResponse;
import quartet.server.api.certification.dto.response.CertificationResponse;
import quartet.server.api.certification.dto.response.CertificationSearchResponse;
import quartet.server.api.certification.service.CertificationService;
import quartet.server.api.common.response.ApiResponse;
import quartet.server.core.code.CommonSuccessCode;
import quartet.server.utils.fixture.Certification.CertificationFixture;
import quartet.server.utils.fixture.Certification.CertificationCategoryFixture;
import quartet.server.utils.fixture.Pageable.PageableFixture;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CertificationController.class)
public class CertificationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    CertificationService certificationService;

    @Test
    @DisplayName("메인페이지에 기본으로 나타나는 상위 자격증 카테고리를 조회한다")
    void success_getCategories_withDefault() throws Exception {
        // given
        final boolean isDefault = true;
        final List<CertificationCategoriesResponse> responses = CertificationCategoryFixture.categoryResList();
        final ApiResponse<List<CertificationCategoriesResponse>> expectedResponse = ApiResponse.success(
                CommonSuccessCode.OK, responses);
        when(certificationService.getCategories(eq(isDefault))).thenReturn(responses);

        // when
        final String result = mockMvc.perform(
                get("/api/v1/certifications/category")
                        .param("isDefault", String.valueOf(isDefault))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        final String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        assertThat(result).isEqualTo(expectedJson);
        verify(certificationService, never()).getCategories(anyLong());
        verify(certificationService, times(1)).getCategories(isDefault);
    }

    @Test
    @DisplayName("메인페이지에 더보기 클릭시 나타나는 기타 상위 자격증 카테고리를 조회한다")
    void success_getCategories_withExtra() throws Exception {
        // given
        final boolean isDefault = false;
        final List<CertificationCategoriesResponse> responses = CertificationCategoryFixture.categoryResList();
        final ApiResponse<List<CertificationCategoriesResponse>> expectedResponse = ApiResponse.success(
                CommonSuccessCode.OK, responses);
        when(certificationService.getCategories(eq(isDefault))).thenReturn(responses);

        // when
        final String result = mockMvc.perform(
                get("/api/v1/certifications/category")
                        .param("isDefault", String.valueOf(isDefault))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        final String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        assertThat(result).isEqualTo(expectedJson);
        verify(certificationService, never()).getCategories(anyLong());
        verify(certificationService, times(1)).getCategories(isDefault);
    }

    @Test
    @DisplayName("특정 상위 카테고리에 속하는 하위 자격증 카테고리를 조회한다")
    void success_getCategories_byMainCategory() throws Exception {
        // given
        final long mainCategoryId = 1L;
        final List<CertificationCategoriesResponse> responses = CertificationCategoryFixture.categoryResList();
        final ApiResponse<List<CertificationCategoriesResponse>> expectedResponse = ApiResponse.success(
                CommonSuccessCode.OK, responses);
        when(certificationService.getCategories(eq(mainCategoryId))).thenReturn(responses);

        // when
        final String result = mockMvc.perform(
                get("/api/v1/certifications/category")
                        .param("mainCategoryId", String.valueOf(mainCategoryId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        final String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        assertThat(result).isEqualTo(expectedJson);
        verify(certificationService, times(1)).getCategories(mainCategoryId);
        verify(certificationService, never()).getCategories(anyBoolean());
    }

    @Test
    @DisplayName("특정 자격증 카테고리에 속하는 자격증들을 조회한다")
    void success_getAllCertificationByCategoryTest() throws Exception {
        // given
        final long categoryId = 1L;
        final Pageable pageable = PageableFixture.pageable(0,10, Sort.by(Sort.Order.asc("id")));
        final List<CertificationSearchResponse> certificationList = List.of(
            new CertificationSearchResponse(1L, "IT", "데이터베이스", "자격증 1", LocalDateTime.parse("2025-05-01T09:00:00Z"), LocalDateTime.parse("2025-05-01T10:00:00Z"), 100),
            new CertificationSearchResponse(2L, "IT", "프로그래밍", "자격증 2", LocalDateTime.parse("2025-05-01T08:00:00Z"), LocalDateTime.parse("2025-05-01T10:00:00Z"), 50)
        );
        final Page<CertificationSearchResponse> responses = new PageImpl<>(certificationList);
        final ApiResponse<Page<CertificationSearchResponse>> expectedResponse = ApiResponse.success(
                CommonSuccessCode.OK, responses);

        when(certificationService.getAllCertificationsByCategory(
                eq(categoryId), eq(pageable)
        )).thenReturn(responses);

        // when
        final String result = mockMvc.perform(
                get("/api/v1/certifications")
                        .param("categoryId", String.valueOf(categoryId))
                        .param("page", "0")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        final String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        assertThat(result).isEqualTo(expectedJson);
        verify(certificationService, times(1)).getAllCertificationsByCategory(categoryId,pageable);
    }

    @Test
    @DisplayName("자격증에 대한 상세 정보를 조회한다")
    void success_getCertification() throws Exception {
        // given
        final long certificationId  = 1L;
        final CertificationResponse responses = CertificationFixture.certificationRes(certificationId);
        final ApiResponse<CertificationResponse> expectedResponse = ApiResponse.success(
                CommonSuccessCode.OK, responses);
        when(certificationService.getCertification(eq(certificationId))).thenReturn(responses);

        // when
        final String result = mockMvc.perform(
                get("/api/v1/certifications/{certificationId}",certificationId)
                       .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        // then
        final String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        assertThat(result).isEqualTo(expectedJson);
        verify(certificationService, times(1)).getCertification(certificationId);
    }

    @Test
    @DisplayName("추천 자격증을 조회한다")
    void success_getRecommendedCertifications() throws Exception {
        // given
        final long memberId = 1L;
        final List<CertificationSearchResponse> responses = List.of(
            new CertificationSearchResponse(1L, "IT", "데이터베이스", "자격증 1", LocalDateTime.parse("2025-05-01T09:00:00Z"), LocalDateTime.parse("2025-05-01T10:00:00Z"), 100),
            new CertificationSearchResponse(2L, "IT", "프로그래밍", "자격증 2", LocalDateTime.parse("2025-05-01T08:00:00Z"), LocalDateTime.parse("2025-05-01T10:00:00Z"), 50)
        );
        final ApiResponse<List<CertificationSearchResponse>> expectedResponse = ApiResponse.success(
                CommonSuccessCode.OK, responses);
        when(certificationService.getRecommendedCertifications(eq(memberId))).thenReturn(responses);

        // when
        final String result = mockMvc.perform(
                        get("/api/v1/certifications/recommendation")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        final String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        assertThat(result).isEqualTo(expectedJson);
        verify(certificationService, times(1)).getRecommendedCertifications(memberId);
    }
}
