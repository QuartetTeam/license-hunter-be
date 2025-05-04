package quartet.server.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import quartet.server.domain.member.model.MemberCategory;
import java.util.List;

@Repository
public interface MemberCategoryRepository extends JpaRepository<MemberCategory, Long> {
    List<MemberCategory> findByMemberId(Long memberId);

    @Transactional
    void deleteByMemberId(Long memberId);
}
