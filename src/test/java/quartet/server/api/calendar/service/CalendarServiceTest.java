package quartet.server.api.calendar.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import quartet.server.api.calendar.dto.response.CalendarProjection;
import quartet.server.api.calendar.dto.response.CalendarResponse;
import quartet.server.api.calendar.fixture.CalendarFixture;
import quartet.server.api.calendar.query.CalendarQueryRepository;
import quartet.server.domain.calender.exception.CalendarNotFoundException;
import quartet.server.domain.calender.model.Calendar;
import quartet.server.domain.calender.repository.CalendarRepository;
import quartet.server.domain.certification.model.Certification;
import quartet.server.domain.certification.repository.CertificationRepository;
import quartet.server.domain.member.exception.MemberNotFoundException;
import quartet.server.domain.member.model.Member;
import quartet.server.domain.member.repository.MemberRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @InjectMocks
    private CalendarService calendarService;

    @Mock
    private CalendarRepository calendarRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CertificationRepository certificationRepository;

    @Mock
    private CalendarQueryRepository calendarQueryRepository;

    @Nested
    @DisplayName("캘런더 목록 조회")
    class GetCalendarsByMemberIdTest {

        @Test
        public void testGetCalendarsByMemberId() {
            long memberId = 1L;

            LocalDate baseDate = LocalDate.of(2024, 2, 1);
            ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

            Instant dbStartDate = baseDate.minusMonths(2).atStartOfDay(SEOUL_ZONE).toInstant(); // 2023-12-01T00:00:00Z
            Instant dbEndDate = baseDate.plusYears(1).plusMonths(2).atStartOfDay(SEOUL_ZONE).toInstant(); // 2025-04-01T00:00:00Z

            List<CalendarProjection> calendarProjections = CalendarFixture.calendarProjections();
            List<CalendarResponse> expectedResponses = CalendarFixture.calendarResponses();

            when(calendarQueryRepository.findCalendarProjectionsByMemberIdAndDateRange(eq(memberId), eq(dbStartDate), eq(dbEndDate)))
                    .thenReturn(calendarProjections.stream());

            // when
            List<CalendarResponse> actualResponses = calendarService.getCalendarsByMemberId(memberId, baseDate);

            // then
            assertThat(actualResponses)
                    .usingRecursiveComparison()
                    .ignoringCollectionOrder()
                    .isEqualTo(expectedResponses);
        }

        @Test
        public void testGetCalendarsByMemberId_withNoProjections() {
            long memberId = 1L;
            when(calendarQueryRepository.findCalendarProjectionsByMemberIdAndDateRange(eq(memberId), any(), any()))
                    .thenReturn(Stream.empty());

            List<CalendarResponse> actualResponses = calendarService.getCalendarsByMemberId(memberId, LocalDate.of(2024, 2, 1));

            assertTrue(actualResponses.isEmpty());

            verify(calendarQueryRepository).findCalendarProjectionsByMemberIdAndDateRange(eq(memberId), any(), any());
        }
    }

    @Nested
    @DisplayName("캘린더 구독")
    class SubscribeCalendarTest {

        @Test
        @DisplayName("멤버와 자격증 정보로 캘린더를 구독한다")
        void success_shouldSaveCalendar() {
            // given
            final long memberId = 1L;
            final long certificationId = 2L;
            Member member = mock(Member.class);
            Certification certification = mock(Certification.class);

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(certificationRepository.findById(certificationId)).thenReturn(Optional.of(certification));
            when(calendarRepository.existsByMemberIdAndCertificationId(anyLong(), anyLong())).thenReturn(false);
            when(member.getId()).thenReturn(memberId);
            when(certification.getId()).thenReturn(certificationId);

            // when
            calendarService.subscribeCalendar(memberId, certificationId);

            // then
            verify(memberRepository).findById(memberId);
            verify(certificationRepository).findById(certificationId);
            verify(calendarRepository).existsByMemberIdAndCertificationId(memberId, certificationId);
            verify(calendarRepository).save(any(Calendar.class));
        }

        @Test
        @DisplayName("존재하지 않는 멤버 ID로 구독 시도 시 예외가 발생한다")
        void fail_whenMemberNotFound() {
            // given
            final long memberId = 1L;
            final long certificationId = 2L;
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> calendarService.subscribeCalendar(memberId, certificationId))
                    .isInstanceOf(MemberNotFoundException.class);
            verify(memberRepository).findById(memberId);
            verify(certificationRepository, never()).findById(anyLong());
            verify(calendarRepository, never()).save(any());
        }

        @Test
        @DisplayName("존재하지 않는 자격증 ID로 구독 시도 시 예외가 발생한다")
        void fail_whenCertificationNotFound() {
            // given
            final long memberId = 1L;
            final long certificationId = 2L;
            Member member = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(certificationRepository.findById(certificationId)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> calendarService.subscribeCalendar(memberId, certificationId))
                    .isInstanceOf(CalendarNotFoundException.class);
            verify(memberRepository).findById(memberId);
            verify(certificationRepository).findById(certificationId);
            verify(calendarRepository, never()).save(any());
        }

    }
    @Nested
    @DisplayName("캘린더 구독 취소")
    class UnsubscribeCalendarTest {

        @Test
        @DisplayName("캘린더 ID와 멤버 ID로 캘린더 구독을 취소한다")
        void success_shouldDeleteCalendar() {
            // given
            final long memberId = 1L;
            final long certificationId = 2L;
            Calendar calendar = mock(Calendar.class);

            when(calendarRepository.findByMemberIdAndCertificationId(memberId, certificationId))
                    .thenReturn(Optional.of(calendar));

            // when
            calendarService.unsubscribeCalendar(memberId, certificationId);

            // then
            verify(calendarRepository).findByMemberIdAndCertificationId(memberId, certificationId);
            verify(calendarRepository).delete(calendar);
        }

        @Test
        @DisplayName("캘린더 ID와 멤버 ID가 일치하는 캘린더가 없을 경우, 캘린더 구독 취소 요청 시 예외가 발생한다")
        void fail_whenCalendarNotFound() {
            // given
            final long memberId = 1L;
            final long certificationId = 2L;

            when(calendarRepository.findByMemberIdAndCertificationId(memberId, certificationId))
                    .thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> calendarService.unsubscribeCalendar(memberId, certificationId))
                    .isInstanceOf(CalendarNotFoundException.class);
            verify(calendarRepository).findByMemberIdAndCertificationId(memberId, certificationId);
            verify(calendarRepository, never()).delete(any());
        }
    }
}