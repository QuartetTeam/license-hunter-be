package quartet.server.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import quartet.server.domain.auth.model.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Boolean existsByToken(String refresh);

    @Transactional
    void deleteByToken(String refresh);

    @Transactional
    void deleteByMemberId(Long memberId);
}
