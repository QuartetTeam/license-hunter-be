package quartet.server.api.calendar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import quartet.server.api.calendar.dto.response.CalendarResponse;
import quartet.server.api.calendar.fixture.CalendarFixture;
import quartet.server.api.calendar.service.CalendarService;
import quartet.server.api.common.response.ApiResponse;
import quartet.server.core.code.CommonSuccessCode;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalendarController.class)
class CalendarControllerTest { // TODO 박현제: 인증/인가 완성 후 추가 테스트 작성 예정

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CalendarService calendarService;

    @Test
    @DisplayName("현재 사용자의 캘린더 목록을 조회한다")
    void success_getCurrentMemberCalendars_shouldReturnCalendarResponses() throws Exception {
        // given

        final long memberId = 1L;
        final LocalDate baseDate = LocalDate.of(2024, 2, 1); // 테스트 고정 날짜
        final List<CalendarResponse> responses = CalendarFixture.calendarResponses();

        final ApiResponse<List<CalendarResponse>> expectedResponse = ApiResponse.success(CommonSuccessCode.OK, responses);

        when(calendarService.getCalendarsByMemberId(eq(memberId), eq(baseDate)))
                .thenReturn(responses);
        // when
        final String result = mockMvc.perform(get("/api/v1/calendars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        final String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        assertThat(result).isEqualTo(expectedJson);
        verify(calendarService, times(1)).getCalendarsByMemberId(1L, baseDate);
    }

    @Test
    @DisplayName("자격증 캘린더를 구독한다")
    void success_subscribeCalendar_shouldReturnCreatedStatus() throws Exception {
        // given
        final long certificationId = 1L;
        final ApiResponse<Void> expectedResponse = ApiResponse.success(CommonSuccessCode.CREATED);
        doNothing().when(calendarService).subscribeCalendar(anyLong(), eq(certificationId));

        // when
        final String result = mockMvc.perform(post("/api/v1/certifications/{certificationId}/calendars", certificationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        final String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        assertThat(result).isEqualTo(expectedJson);
        verify(calendarService, times(1)).subscribeCalendar(1L, certificationId);
    }

    @Test
    @DisplayName("자격증 캘린더 구독을 취소한다")
    void success_unsubscribeCalendar_shouldReturnNoContentStatus() throws Exception {
        // given
        final long certificationId = 1L;
        final ApiResponse<Void> expectedResponse = ApiResponse.success(CommonSuccessCode.NO_CONTENT);
        doNothing().when(calendarService).unsubscribeCalendar(anyLong(), eq(certificationId));

        // when
        final String result = mockMvc.perform(delete("/api/v1/certifications/{certificationId}/calendars", certificationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        final String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        assertThat(result).isEqualTo(expectedJson);
        verify(calendarService, times(1)).unsubscribeCalendar(1L, certificationId);
    }
}