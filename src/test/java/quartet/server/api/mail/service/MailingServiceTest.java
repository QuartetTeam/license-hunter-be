package quartet.server.api.mail.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import quartet.server.api.certification.fixture.CertificationFixture;
import quartet.server.api.mail.dto.response.MailingResponse;
import quartet.server.api.mail.fixture.MailingFixture;
import quartet.server.api.mail.query.MailingQueryRepository;
import quartet.server.api.member.fixture.MemberFixture;
import quartet.server.domain.certification.exception.CertificationNotFoundException;
import quartet.server.domain.certification.model.Certification;
import quartet.server.domain.certification.repository.CertificationRepository;
import quartet.server.domain.mail.exception.MailingAlreadyExistsException;
import quartet.server.domain.mail.model.Mailing;
import quartet.server.domain.mail.repository.MailingRepository;
import quartet.server.domain.member.exception.MemberNotFoundException;
import quartet.server.domain.member.model.Member;
import quartet.server.domain.member.repository.MemberRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailingServiceTest {

    @InjectMocks
    private MailingService mailingService;

    @Mock
    private MailingRepository mailingRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CertificationRepository certificationRepository;

    @Mock
    private MailingQueryRepository mailingQueryRepository;

    @Nested
    @DisplayName("메일링 구독 목록 조회")
    class GetMailingsByMemberIdTest {

        @Test
        @DisplayName("멤버 ID로 구독 목록을 조회한다")
        void success_shouldReturnMailingResponses() {
            // given
            final long memberId = 1L;
            final Pageable pageable = PageRequest.of(0, 4);
            final List<MailingResponse> expectedResponses = MailingFixture.mailingResponses();
            final Page<MailingResponse> expectedPage = new PageImpl<>(expectedResponses, pageable, expectedResponses.size());

            final Instant startDate = LocalDate.now().atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();

            when(mailingQueryRepository.getMailingsByMemberId(eq(memberId), eq(startDate), eq(pageable)))
                    .thenReturn(expectedPage);

            // when
            final Page<MailingResponse> actualPage = mailingService.getMailingsByMemberId(memberId, pageable);

            // then
            assertThat(actualPage.getContent()).isEqualTo(expectedResponses);
            assertThat(actualPage.getTotalElements()).isEqualTo(expectedResponses.size());
            verify(mailingQueryRepository).getMailingsByMemberId(eq(memberId), eq(startDate), eq(pageable));
        }
    }

    @Nested
    @DisplayName("메일링 구독")
    class SubscribeMailingTest {

        @Test
        @DisplayName("멤버와 자격증 정보로 메일링을 구독한다")
        void success_shouldSaveMailing() {
            // given
            final long memberId = 1L;
            final long certificationId = 2L;
            final Member member = MemberFixture.createMember();
            final Certification certification = CertificationFixture.createCertification();

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(certificationRepository.findById(certificationId)).thenReturn(Optional.of(certification));
            when(mailingRepository.existsByMemberIdAndCertificationId(memberId, certificationId)).thenReturn(false);
            when(mailingRepository.save(any(Mailing.class))).thenReturn(MailingFixture.createMailing());

            // when
            mailingService.subscribeMailing(memberId, certificationId);

            // then
            verify(memberRepository).findById(memberId);
            verify(certificationRepository).findById(certificationId);
            verify(mailingRepository).existsByMemberIdAndCertificationId(memberId, certificationId);
            verify(mailingRepository).save(any(Mailing.class));
        }

        @Test
        @DisplayName("존재하지 않는 멤버 ID로 구독 시도 시 예외가 발생한다")
        void fail_whenMemberNotFound() {
            // given
            final long memberId = 1L;
            final long certificationId = 2L;

            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> mailingService.subscribeMailing(memberId, certificationId))
                    .isInstanceOf(MemberNotFoundException.class);
            verify(memberRepository).findById(memberId);
            verify(certificationRepository, never()).findById(anyLong());
            verify(mailingRepository, never()).save(any());
        }

        @Test
        @DisplayName("존재하지 않는 자격증 ID로 구독 시도 시 예외가 발생한다")
        void fail_whenCertificationNotFound() {
            // given
            final long memberId = 1L;
            final long certificationId = 2L;
            final Member member = MemberFixture.createMember();

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(certificationRepository.findById(certificationId)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> mailingService.subscribeMailing(memberId, certificationId))
                    .isInstanceOf(CertificationNotFoundException.class);
            verify(memberRepository).findById(memberId);
            verify(certificationRepository).findById(certificationId);
            verify(mailingRepository, never()).save(any());
        }

        @Test
        @DisplayName("이미 구독된 경우 예외가 발생한다")
        void fail_whenMailingAlreadyExists() {
            // given
            final long memberId = 1L;
            final long certificationId = 2L;
            final Member member = MemberFixture.createMember();
            final Certification certification = CertificationFixture.createCertification();

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(certificationRepository.findById(certificationId)).thenReturn(Optional.of(certification));
            when(mailingRepository.existsByMemberIdAndCertificationId(memberId, certificationId)).thenReturn(true);

            // when, then
            assertThatThrownBy(() -> mailingService.subscribeMailing(memberId, certificationId))
                    .isInstanceOf(MailingAlreadyExistsException.class);
            verify(memberRepository).findById(memberId);
            verify(certificationRepository).findById(certificationId);
            verify(mailingRepository).existsByMemberIdAndCertificationId(memberId, certificationId);
            verify(mailingRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("메일링 구독 취소")
    class UnsubscribeMailingsTest {

        @Test
        @DisplayName("멤버 ID와 구독 ID 목록으로 메일링 구독을 취소한다")
        void success_shouldDeleteMailings() {
            // given
            final long memberId = 1L;
            final List<Long> mailingIds = List.of(1L, 2L);
            final List<Mailing> mailings = List.of(MailingFixture.createMailing());

            when(mailingRepository.findByMemberIdAndIdIn(memberId, mailingIds)).thenReturn(mailings);

            // when
            mailingService.unsubscribeMailings(memberId, mailingIds);

            // then
            verify(mailingRepository).findByMemberIdAndIdIn(memberId, mailingIds);
            verify(mailingRepository).deleteAll(mailings);
        }

        @Test
        @DisplayName("구독 ID 목록에 해당하는 메일링이 없는 경우 빈 리스트로 처리된다")
        void success_whenNoMailingsFound() {
            // given
            final long memberId = 1L;
            final List<Long> mailingIds = List.of(1L, 2L);

            when(mailingRepository.findByMemberIdAndIdIn(memberId, mailingIds)).thenReturn(List.of());

            // when
            mailingService.unsubscribeMailings(memberId, mailingIds);

            // then
            verify(mailingRepository).findByMemberIdAndIdIn(memberId, mailingIds);
            verify(mailingRepository).deleteAll(List.of());
        }
    }
}