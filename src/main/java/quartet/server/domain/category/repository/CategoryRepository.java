package quartet.server.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quartet.server.domain.category.model.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentCategory_Id(Long parentId);
    List<Category> findByIsDefaultTrue();
    List<Category> findByIsDefaultFalseAndParentCategoryIsNull();
}