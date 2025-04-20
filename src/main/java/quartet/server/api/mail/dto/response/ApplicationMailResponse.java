package quartet.server.api.mail.dto.response;

import quartet.server.domain.certification.type.ExamType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record ApplicationMailResponse(
        Long memberId,
        String userName,
        String email,
        List<CertificationInfoResponse> certifications
) {
    public ApplicationMailResponse(
            Long memberId,
            String userName,
            String email)
    {
        this(
                memberId,
                userName,
                email,
                new ArrayList<>()
        );
    }

    public static ApplicationMailResponse of(
            Long memberId,
            String userName,
            String email
    ) {
        return new ApplicationMailResponse(
                memberId,
                userName,
                email
        );
    }

    public void addCertificationInfo(CertificationInfoResponse response) {
        certifications.add(response);
    }

    public record CertificationInfoResponse(
            Long certificationId,
            String certificationName,
            Instant date,
            String applicationUrl,
            ExamType examType
    ) {
        public static CertificationInfoResponse of(
                Long certificationId,
                String certificationName,
                Instant date,
                String applicationUrl,
                ExamType examType
        ) {
            return new CertificationInfoResponse(
                    certificationId,
                    certificationName,
                    date,
                    applicationUrl,
                    examType
            );
        }
    }
}
