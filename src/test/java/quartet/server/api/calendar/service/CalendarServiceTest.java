package quartet.server.api.calendar.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import quartet.server.api.calendar.dto.response.CalendarResponse;
import quartet.server.api.calendar.fixture.CalendarFixture;
import quartet.server.api.calendar.query.CalendarQueryRepository;
import quartet.server.domain.calender.exception.CalendarAlreadyExistsException;
import quartet.server.domain.calender.exception.CalendarNotFoundException;
import quartet.server.domain.calender.model.Calendar;
import quartet.server.domain.calender.repository.CalendarRepository;
import quartet.server.domain.certification.exception.CertificationNotFoundException;
import quartet.server.domain.certification.model.Certification;
import quartet.server.domain.certification.repository.CertificationRepository;
import quartet.server.domain.example.exception.ExampleNotFoundException;
import quartet.server.domain.member.exception.MemberNotFoundException;
import quartet.server.domain.member.model.Member;
import quartet.server.domain.member.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        @DisplayName("멤버 ID로 캘린더 목록을 조회할 수 있다")
        void getCalendarsByMemberId_ShouldReturnCalendarList() {
            // given
            final long memberId = 1L;
            final List<CalendarResponse> expectedResponses = CalendarFixture.calendarResponses();

            when(calendarQueryRepository.findCalendarsByMemberId(memberId)).thenReturn(expectedResponses);

            // when
            final List<CalendarResponse> actualResponses = calendarService.getCalendarsByMemberId(memberId);

            // then
            assertThat(actualResponses).isEqualTo(expectedResponses);
            verify(calendarQueryRepository).findCalendarsByMemberId(memberId);
        }
    }

    @Nested
    @DisplayName("캘린더 구독")
    class SubscribeCalendarTest {

        @Test
        @DisplayName("멤버와 자격증 정보로 캘린더를 구독할 수 있다")
        void subscribeCalendar_ShouldSaveCalendar() {
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
        void subscribeCalendar_WithNonExistentMember_ShouldThrowException() {
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
        void subscribeCalendar_WithNonExistentCertification_ShouldThrowException() {
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

        @Test
        @DisplayName("이미 구독 중인 캘린더를 다시 구독 시도 시 예외가 발생한다")
        void subscribeCalendar_WithExistingCalendar_ShouldThrowException() {
            // given
            final long memberId = 1L;
            final long certificationId = 2L;
            Member member = mock(Member.class);
            Certification certification = mock(Certification.class);

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(certificationRepository.findById(certificationId)).thenReturn(Optional.of(certification));
            when(calendarRepository.existsByMemberIdAndCertificationId(anyLong(), anyLong())).thenReturn(true);
            when(member.getId()).thenReturn(memberId);
            when(certification.getId()).thenReturn(certificationId);

            // when, then
            assertThatThrownBy(() -> calendarService.subscribeCalendar(memberId, certificationId))
                    .isInstanceOf(CalendarAlreadyExistsException.class);
            verify(memberRepository).findById(memberId);
            verify(certificationRepository).findById(certificationId);
            verify(calendarRepository).existsByMemberIdAndCertificationId(memberId, certificationId);
            verify(calendarRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("캘린더 구독 취소")
    class UnsubscribeCalendarTest {

        @Test
        @DisplayName("멤버와 자격증 정보로 캘린더 구독을 취소할 수 있다")
        void unsubscribeCalendar_ShouldDeleteCalendar() {
            // given
            final long memberId = 1L;
            final long certificationId = 2L;
            Member member = mock(Member.class);
            Certification certification = mock(Certification.class);
            Calendar calendar = mock(Calendar.class);

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(certificationRepository.findById(certificationId)).thenReturn(Optional.of(certification));
            when(member.getId()).thenReturn(memberId);
            when(certification.getId()).thenReturn(certificationId);
            when(calendarRepository.findByMemberIdAndCertificationId(memberId, certificationId)).thenReturn(Optional.of(calendar));

            // when
            calendarService.unsubscribeCalendar(memberId, certificationId);

            // then
            verify(memberRepository).findById(memberId);
            verify(certificationRepository).findById(certificationId);
            verify(calendarRepository).findByMemberIdAndCertificationId(memberId, certificationId);
            verify(calendarRepository).delete(calendar);
        }

        @Test
        @DisplayName("존재하지 않는 멤버 ID로 구독 취소 시도 시 예외가 발생한다")
        void unsubscribeCalendar_WithNonExistentMember_ShouldThrowException() {
            // given
            final long memberId = 1L;
            final long certificationId = 2L;
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> calendarService.unsubscribeCalendar(memberId, certificationId))
                    .isInstanceOf(MemberNotFoundException.class);
            verify(memberRepository).findById(memberId);
            verify(certificationRepository, never()).findById(anyLong());
            verify(calendarRepository, never()).delete(any());
        }

        @Test
        @DisplayName("존재하지 않는 자격증 ID로 구독 취소 시도 시 예외가 발생한다")
        void unsubscribeCalendar_WithNonExistentCertification_ShouldThrowException() {
            // given
            final long memberId = 1L;
            final long certificationId = 2L;
            Member member = mock(Member.class);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(certificationRepository.findById(certificationId)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> calendarService.unsubscribeCalendar(memberId, certificationId))
                    .isInstanceOf(CertificationNotFoundException.class);
            verify(memberRepository).findById(memberId);
            verify(certificationRepository).findById(certificationId);
            verify(calendarRepository, never()).delete(any());
        }

        @Test
        @DisplayName("구독하지 않은 캘린더 구독 취소 시도 시 예외가 발생한다")
        void unsubscribeCalendar_WithNonExistentCalendar_ShouldThrowException() {
            // given
            final long memberId = 1L;
            final long certificationId = 2L;
            Member member = mock(Member.class);
            Certification certification = mock(Certification.class);

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(certificationRepository.findById(certificationId)).thenReturn(Optional.of(certification));
            when(member.getId()).thenReturn(memberId);
            when(certification.getId()).thenReturn(certificationId);
            when(calendarRepository.findByMemberIdAndCertificationId(memberId, certificationId)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> calendarService.unsubscribeCalendar(memberId, certificationId))
                    .isInstanceOf(CalendarNotFoundException.class);
            verify(memberRepository).findById(memberId);
            verify(certificationRepository).findById(certificationId);
            verify(calendarRepository).findByMemberIdAndCertificationId(memberId, certificationId);
            verify(calendarRepository, never()).delete(any());
        }
    }
}