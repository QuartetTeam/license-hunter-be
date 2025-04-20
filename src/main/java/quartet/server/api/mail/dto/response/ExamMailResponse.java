package quartet.server.api.mail.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import quartet.server.domain.certification.type.ExamType;

import java.time.Instant;

public record ExamMailResponse(
    Long memberId,
    Long certificationId,
    String userName,
    String email,
    String certificationName,
    Instant examDate,
    ExamType examType
) {
    @QueryProjection
    public ExamMailResponse(
        Long memberId,
        Long certificationId,
        String userName,
        String email,
        String certificationName,
        Instant examDate,
        ExamType examType
    ) {
        this.memberId = memberId;
        this.certificationId = certificationId;
        this.userName = userName;
        this.email = email;
        this.certificationName = certificationName;
        this.examDate = examDate;
        this.examType = examType;
    }
}