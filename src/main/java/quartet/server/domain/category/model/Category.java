package quartet.server.domain.category.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.IdentifiableEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
public class Category extends IdentifiableEntity {

    @Column(nullable = false, length = 25)
    @Comment("카테고리 이름")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    @Comment("부모 카테고리 ID")
    private Category parentCategory;

    @Comment("디폴트 디스플레이 여부")
    private Boolean isDefault = false;

    private Category(final String name, final Category parentCategory) {
        this.name = name;
        this.parentCategory = parentCategory;
    }

    public static Category of(final String name, final Category parentCategory) {
        return new Category(name, parentCategory);
    }
}
