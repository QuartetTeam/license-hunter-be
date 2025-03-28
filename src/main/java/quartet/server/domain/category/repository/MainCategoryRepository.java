package quartet.server.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartet.server.domain.category.model.MainCategory;

import java.util.List;

@Repository
public interface MainCategoryRepository extends JpaRepository<MainCategory, Long> {
    List<MainCategory> findByIsDefaultTrue();
    List<MainCategory> findByIsDefaultFalse();
} 