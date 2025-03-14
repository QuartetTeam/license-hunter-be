package quartet.server.api.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quartet.server.api.mail.dto.response.MailingResponse;
import quartet.server.api.mail.query.MailingQueryRepository;
import quartet.server.domain.certification.exception.CertificationNotFoundException;
import quartet.server.domain.certification.model.Certification;
import quartet.server.domain.certification.repository.CertificationRepository;
import quartet.server.domain.mail.exception.MailingAlreadyExistsException;
import quartet.server.domain.mail.model.Mailing;
import quartet.server.domain.mail.repository.MailingRepository;
import quartet.server.domain.member.exception.MemberNotFoundException;
import quartet.server.domain.member.model.Member;
import quartet.server.domain.member.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MailingService {
    
    private final MailingRepository mailingRepository;
    private final MemberRepository memberRepository;
    private final CertificationRepository certificationRepository;
    private final MailingQueryRepository mailingQueryRepository;

    public Page<MailingResponse> getMailingsByMemberId(final long memberId, final Pageable pageable) {

        return mailingQueryRepository.getMailingsByMemberId(memberId, pageable);
    }

    @Transactional
    public void subscribeMailing(final long memberId, final long certificationId) {
        final Member member = getMemberById(memberId);
        final Certification certification = getCertificationById(certificationId);

        if (mailingRepository.existsByMemberIdAndCertificationId(memberId, certificationId)) {
            throw new MailingAlreadyExistsException();
        }

        mailingRepository.save(Mailing.of(member, certification));
    }

    @Transactional
    public void unsubscribeMailings(final long memberId, final List<Long> mailSubscriptionIds) {
        final List<Mailing> mailing = mailingRepository.findByMemberIdAndIdIn(memberId, mailSubscriptionIds);

        mailingRepository.deleteAll(mailing);
    }

    private Member getMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Certification getCertificationById(final Long certificationId) {
        return certificationRepository.findById(certificationId)
                .orElseThrow(CertificationNotFoundException::new);
    }
}