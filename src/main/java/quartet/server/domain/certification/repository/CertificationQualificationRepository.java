package quartet.server.domain.certification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import quartet.server.domain.certification.model.CertificationQualification;

public interface CertificationQualificationRepository extends JpaRepository<CertificationQualification, Long> {
} 