package quartet.server.api.member.dto.response;

import quartet.server.domain.mail.type.MailingStatus;

public record MemberMailingStatusResponse(
        MailingStatus status
) {
}