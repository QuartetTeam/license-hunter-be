package quartet.server.domain.certification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartet.server.domain.certification.model.CertificationViewLog;


@Repository
public interface CertificationViewLogRepository extends JpaRepository<CertificationViewLog, Long> {
}