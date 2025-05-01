package quartet.server.domain.category.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.IdentifiableEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "main_category")
public class MainCategory extends IdentifiableEntity {
    @Column(nullable = false, length = 25)
    @Comment("카테고리 이름")
    private String name;

    @Column(nullable = false)
    @Comment("기본 카테고리 여부")
    private boolean isDefault;

    @OneToMany(mappedBy = "mainCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubCategory> subCategories = new ArrayList<>();

    private MainCategory(final String name, final boolean isDefault) {
        this.name = name;
        this.isDefault = isDefault;
    }

    public static MainCategory of(final String name, final boolean isDefault) {
        return new MainCategory(name, isDefault);
    }
} 