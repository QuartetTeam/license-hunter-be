package quartet.server.api.mail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import quartet.server.api.common.response.ApiResponse;
import quartet.server.api.common.response.PageResponse;
import quartet.server.api.mail.dto.response.MailingResponse;
import quartet.server.api.mail.fixture.MailingFixture;
import quartet.server.api.mail.service.MailingService;
import quartet.server.core.code.CommonSuccessCode;
import quartet.server.utils.fixture.Pageable.PageableFixture;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MailingController.class)
class MailingControllerTest { // TODO 박현제: 인증/인가 완성 후 추가 테스트 작성 예정

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MailingService mailingService;

    @Test
    @DisplayName("현재 사용자의 메일링 구독 목록을 조회한다")
    void success_getMemberMailings_shouldReturnMailingResponses() throws Exception {
        // given
        final long memberId = 1L;
        final Pageable pageable = PageableFixture.pageable(4);
        final List<MailingResponse> responses = MailingFixture.mailingResponses();
        final Page<MailingResponse> mailingPage = new PageImpl<>(responses, pageable, responses.size());

        // PageResponse 객체로 변환
        final PageResponse<MailingResponse> pageResponse = PageResponse.from(mailingPage);
        final ApiResponse<PageResponse<MailingResponse>> expectedResponse = ApiResponse.success(CommonSuccessCode.OK, pageResponse);

        when(mailingService.getMailingsByMemberId(memberId, pageable)).thenReturn(mailingPage);

        // when
        final String result = mockMvc.perform(get("/api/v1/mailings")
                        .param("page", "0")
                        .param("pageSize", "4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        final String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        assertThat(result).isEqualTo(expectedJson);
        verify(mailingService, times(1)).getMailingsByMemberId(memberId, pageable);
    }

    @Test
    @DisplayName("쿼리 파라미터를 제공하지 않으면 기본값으로 메일링 구독 목록을 조회한다")
    void success_getMemberMailings_whenNoQueryParamsProvided() throws Exception {
        // given
        final long memberId = 1L;
        final Pageable pageable = PageableFixture.pageable(4);
        final List<MailingResponse> responses = MailingFixture.mailingResponses();
        final Page<MailingResponse> mailingPage = new PageImpl<>(responses, pageable, responses.size());

        // PageResponse 객체로 변환
        final PageResponse<MailingResponse> pageResponse = PageResponse.from(mailingPage);
        final ApiResponse<PageResponse<MailingResponse>> expectedResponse = ApiResponse.success(CommonSuccessCode.OK, pageResponse);

        when(mailingService.getMailingsByMemberId(memberId, pageable)).thenReturn(mailingPage);

        // when
        final String result = mockMvc.perform(get("/api/v1/mailings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        final String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        assertThat(result).isEqualTo(expectedJson);
        verify(mailingService, times(1)).getMailingsByMemberId(memberId, pageable);
    }

    @Test
    @DisplayName("자격증 메일링을 구독한다")
    void success_subscribeMailing_shouldReturnCreatedStatus() throws Exception {
        // given
        final long memberId = 1L;
        final long certificationId = 2L;
        final ApiResponse<Void> expectedResponse = ApiResponse.success(CommonSuccessCode.CREATED);

        doNothing().when(mailingService).subscribeMailing(anyLong(), eq(certificationId));

        // when
        final String result = mockMvc.perform(post("/api/v1/certifications/{certificationId}/mailings", certificationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        final String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        assertThat(result).isEqualTo(expectedJson);
        verify(mailingService, times(1)).subscribeMailing(memberId, certificationId);
    }

    @Test
    @DisplayName("자격증 메일링 구독을 취소한다")
    void success_unsubscribeMailings_shouldReturnNoContentStatus() throws Exception {
        // given
        final long memberId = 1L;
        final List<Long> mailingIds = List.of(1L, 2L);
        final ApiResponse<Void> expectedResponse = ApiResponse.success(CommonSuccessCode.NO_CONTENT);

        doNothing().when(mailingService).unsubscribeMailings(anyLong(), eq(mailingIds));

        // when
        final String result = mockMvc.perform(delete("/api/v1/mailings")
                        .param("mailingIds", "1", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        final String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        assertThat(result).isEqualTo(expectedJson);
        verify(mailingService, times(1)).unsubscribeMailings(memberId, mailingIds);
    }
}