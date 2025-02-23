package quartet.server.domain.calender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartet.server.domain.calender.model.Calendar;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {
}