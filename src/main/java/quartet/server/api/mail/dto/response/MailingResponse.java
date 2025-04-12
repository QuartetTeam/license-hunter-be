package quartet.server.api.mail.dto.response;


import com.querydsl.core.annotations.QueryProjection;

import java.time.Instant;

public record MailingResponse(
        long mailingId,
        long certificationId,
        String certificationName,
        String applicationDate,
        String examDate
) {
    @QueryProjection
    public MailingResponse(
            long mailingId,
            long certificationId,
            String certificationName,
            Instant applicationDate,
            Instant examDate
    ) {
        this(
                mailingId,
                certificationId,
                certificationName,
                applicationDate != null ? applicationDate.toString() : "",
                examDate != null ? examDate.toString() : ""
        );
    }
}