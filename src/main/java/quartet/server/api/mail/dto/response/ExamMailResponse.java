package quartet.server.api.mail.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import quartet.server.domain.certification.type.ExamType;

import java.time.Instant;
import java.time.LocalDateTime;

public record ExamMailResponse(
    Long memberId,
    Long certificationId,
    String userName,
    String email,
    String certificationName,
    LocalDateTime examDate,
    ExamType examType
) {
    @QueryProjection
    public ExamMailResponse(
        Long memberId,
        Long certificationId,
        String userName,
        String email,
        String certificationName,
        LocalDateTime examDate,
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