package quartet.server.api.mail.dto.response;


import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

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
            LocalDateTime applicationDate,
            LocalDateTime examDate
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