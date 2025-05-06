package quartet.server.api.mail.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import quartet.server.domain.certification.type.ExamType;

import java.time.Instant;
import java.time.LocalDateTime;

public record ApplicationMailProjection(
        Long memberId,
        Long certificationId,
        String userName,
        String email,
        String certificationName,
        LocalDateTime applicationDate,
        String applicationUrl,
        ExamType examType
) {
    @QueryProjection
    public ApplicationMailProjection(
            Long memberId,
            Long certificationId,
            String userName,
            String email,
            String certificationName,
            LocalDateTime applicationDate,
            String applicationUrl,
            ExamType examType
    ) {
        this.memberId = memberId;
        this.certificationId = certificationId;
        this.userName = userName;
        this.email = email;
        this.certificationName = certificationName;
        this.applicationDate = applicationDate;
        this.applicationUrl = applicationUrl;
        this.examType = examType;
    }
}