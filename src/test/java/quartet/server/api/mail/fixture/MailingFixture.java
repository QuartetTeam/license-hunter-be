package quartet.server.api.mail.fixture;

import quartet.server.api.mail.dto.response.MailingResponse;
import quartet.server.domain.certification.model.Certification;
import quartet.server.domain.certification.type.QualificationType;
import quartet.server.domain.mail.model.Mailing;
import quartet.server.domain.member.model.Member;

import java.util.List;

public class MailingFixture {

    public static Member createMember() {
        return Member.of("testSocialId", "GOOGLE", "test@example.com", "TestUser", "profile.jpg", "Intro");
    }

    public static Certification createCertification() {
        return Certification.of("정보처리기사", null, QualificationType.T);
    }

    public static Mailing createMailing() {
        return Mailing.of(createMember(), createCertification());
    }

    public static List<MailingResponse> mailingResponses() {
        return List.of(
                new MailingResponse(1L, 2L, "정보처리기사", "자격증 설명"),
                new MailingResponse(2L, 3L, "SQL 개발자", "SQL 관련 자격증")
        );
    }
}