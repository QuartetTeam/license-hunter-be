package quartet.server.domain.calender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import quartet.server.domain.calender.model.Calendar;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    List<Calendar> findAllByMemberId(Long memberId);

    boolean existsByMemberIdAndCertificationId(Long memberId, Long certificationId);

    boolean existsByIdAndMemberId(Long calendarId, Long memberId);

    Optional<Calendar> findByMemberIdAndCertificationId(Long memberId, Long certificationId);

    @Transactional
    void deleteByMemberId(Long memberId);
}