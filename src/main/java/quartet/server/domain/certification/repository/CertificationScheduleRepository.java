package quartet.server.domain.certification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartet.server.domain.certification.model.CertificationSchedule;


@Repository
public interface CertificationScheduleRepository extends JpaRepository<CertificationSchedule, Long> {
}