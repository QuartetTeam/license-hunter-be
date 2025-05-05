package quartet.server.domain.mail.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import quartet.server.domain.mail.model.Mailing;

import java.util.List;
import java.util.Optional;

@Repository
public interface MailingRepository extends JpaRepository<Mailing, Long> {
    List<Mailing> findByMemberIdAndIdIn(Long memberId, List<Long> ids);
    boolean existsByMemberIdAndCertificationId(Long memberId, Long certificationId);
    @Transactional
    void deleteByMemberId(Long memberId);
}