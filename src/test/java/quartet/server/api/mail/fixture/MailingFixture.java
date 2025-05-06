package quartet.server.api.mail.fixture;

import quartet.server.api.mail.dto.response.MailingResponse;
import quartet.server.core.utils.DateUtils;
import quartet.server.domain.mail.model.Mailing;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static quartet.server.api.certification.fixture.CertificationFixture.createCertification;
import static quartet.server.api.member.fixture.MemberFixture.createMember;

public class MailingFixture {
    public static Mailing createMailing() {
        return Mailing.of(createMember(), createCertification());
    }

    public static List<MailingResponse> mailingResponses() {
        LocalDateTime applicationDate = DateUtils.getTodayStart();
        LocalDateTime examDate = DateUtils.getDateAfterNow(0, 2, 0);

        return List.of(
                new MailingResponse(1L, 2L, "정보처리기사", applicationDate, examDate),
                new MailingResponse(2L, 3L, "SQL 개발자", applicationDate, examDate)
        );
    }
}