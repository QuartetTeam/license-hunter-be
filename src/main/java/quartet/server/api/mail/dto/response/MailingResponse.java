package quartet.server.api.mail.dto.response;


import com.querydsl.core.annotations.QueryProjection;

public record MailingResponse(
        long mailingId,
        long certificationId,
        String certificationName,
        String description
) {
    @QueryProjection
    public MailingResponse {
    }
}