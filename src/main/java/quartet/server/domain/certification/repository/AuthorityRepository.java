package quartet.server.domain.certification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartet.server.domain.certification.model.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}


