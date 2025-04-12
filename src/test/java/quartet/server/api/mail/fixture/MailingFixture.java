package quartet.server.api.mail.fixture;

import quartet.server.api.mail.dto.response.MailingResponse;
import quartet.server.domain.mail.model.Mailing;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static quartet.server.api.certification.fixture.CertificationFixture.createCertification;
import static quartet.server.api.member.fixture.MemberFixture.createMember;

public class MailingFixture {
    public static Mailing createMailing() {
        return Mailing.of(createMember(), createCertification());
    }

    public static List<MailingResponse> mailingResponses() {
        Instant applicationDate = LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
        Instant examDate = LocalDate.now().plusMonths(2).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();

        return List.of(
                new MailingResponse(1L, 2L, "정보처리기사", applicationDate, examDate),
                new MailingResponse(2L, 3L, "SQL 개발자", applicationDate, examDate)
        );
    }
}