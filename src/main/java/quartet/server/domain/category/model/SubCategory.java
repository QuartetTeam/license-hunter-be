package quartet.server.domain.category.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.IdentifiableEntity;
import quartet.server.domain.certification.model.Certification;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sub_category")
public class SubCategory extends IdentifiableEntity {
    @Column(nullable = false, length = 25)
    @Comment("카테고리 이름")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_category_id", nullable = false)
    @Comment("대분류 카테고리")
    private MainCategory mainCategory;

    @OneToMany(mappedBy = "subCategory")
    private List<Certification> certifications = new ArrayList<>();

    private SubCategory(final String name, final MainCategory mainCategory) {
        this.name = name;
        this.mainCategory = mainCategory;
    }

    public static SubCategory of(final String name, final MainCategory mainCategory) {
        return new SubCategory(name, mainCategory);
    }
} 