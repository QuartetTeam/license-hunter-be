package quartet.server.api.certification.fixture;

import quartet.server.domain.certification.model.Certification;
import quartet.server.domain.certification.type.QualificationType;

public class CertificationFixture {

    public static Certification createCertification() {
        return Certification.of("정보처리기사", null, QualificationType.T);
    }
}