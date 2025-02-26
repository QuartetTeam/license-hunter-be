package quartet.server.api.certification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quartet.server.api.certification.dto.response.CertificationRes;
import quartet.server.api.certification.query.CertificationQueryRepository;
import quartet.server.domain.certification.repository.*;

@Service
@RequiredArgsConstructor
public class CertificationService {
    private final AuthorityRepository authorityRepository;
    private final CertificationRepository certificationRepository;
    private final CertificationExamDetailRepository certificationExamRepository;
    private final CertificationPassCriteriaRepository certificationPassCriteriaRepository;
    private final CertificationScheduleRepository certificationScheduleRepository;
    private final CertificationViewLogRepository certificationViewLogRepository;
    private final CertificationQueryRepository certificationQueryRepository;

    public CertificationRes getCertification(long certificationId) {
        return certificationQueryRepository.getCertification(certificationId);
    }
}
