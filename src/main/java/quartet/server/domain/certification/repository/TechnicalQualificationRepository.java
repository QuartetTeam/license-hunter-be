package quartet.server.domain.certification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import quartet.server.domain.certification.model.HrdCertificationDetail;

public interface TechnicalQualificationRepository extends JpaRepository<HrdCertificationDetail, Long> {
} 