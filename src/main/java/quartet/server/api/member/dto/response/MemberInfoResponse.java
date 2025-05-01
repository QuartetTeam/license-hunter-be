package quartet.server.api.member.dto.response;

import quartet.server.domain.mail.type.MailingStatus;

import java.util.List;

public record MemberInfoResponse(
        String email,
        String nickname,
        String profileImageUrl,
        MailingStatus status,
        List<String> interests
) {}