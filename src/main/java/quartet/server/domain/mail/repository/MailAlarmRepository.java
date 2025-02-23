package quartet.server.domain.mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartet.server.domain.mail.model.MailAlarm;

@Repository
public interface MailAlarmRepository extends JpaRepository<MailAlarm, Long> {
}