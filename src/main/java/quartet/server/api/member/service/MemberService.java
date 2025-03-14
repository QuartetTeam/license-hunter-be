package quartet.server.api.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quartet.server.api.member.dto.response.MemberMailingStatusResponse;
import quartet.server.domain.mail.type.MailingStatus;
import quartet.server.domain.member.exception.MemberNotFoundException;
import quartet.server.domain.member.model.Member;
import quartet.server.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberMailingStatusResponse getMailingStatus(final long memberId) {
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        return new MemberMailingStatusResponse(member.getMailingStatus());
    }

    @Transactional
    public MemberMailingStatusResponse updateMailingStatus(final long memberId) {
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        final MailingStatus oppositeStatus = MailingStatus.opposite(member.getMailingStatus());
        member.updateMailingStatus(oppositeStatus);

        return new MemberMailingStatusResponse(oppositeStatus);
    }
}
