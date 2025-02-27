package quartet.server.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartet.server.domain.member.model.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
