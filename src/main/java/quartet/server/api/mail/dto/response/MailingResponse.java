package quartet.server.api.mail.dto.response;


import com.querydsl.core.annotations.QueryProjection;

import java.time.Instant;

public record MailingResponse(
        long mailingId,
        long certificationId,
        String name,
        String applicationDate,
        String examDate
) {
    @QueryProjection
    public MailingResponse(
            long mailingId,
            long certificationId,
            String name,
            Instant applicationDate,
            Instant examDate
    ) {
        this(
                mailingId,
                certificationId,
                name,
                applicationDate != null ? applicationDate.toString() : "",
                examDate != null ? examDate.toString() : ""
        );
    }
}