package quartet.server.domain.certification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.IdentifiableEntity;
import quartet.server.domain.category.model.Category;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "certification")
public class Certification extends IdentifiableEntity {
    @Column(nullable = false, length = 100)
    @Comment("자격증명")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="authority_id", nullable = false)
    @Comment("시행 기관")
    private Authority authority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id",  nullable = false)
    @Comment("자격증 카테고리 대분류")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sub_category_id",  nullable = true)
    @Comment("자격증 카테고리 소분류")
    private Category subCategory;

    @Column(nullable = false)
    @Comment("상세 페이지 조회 수")
    private int viewCount = 0;

    private Certification(final String name, final Authority  authority, final Category category, final Category subCategory){
        this.name = name;
        this.authority = authority;
        this.category = category;
        this.subCategory = subCategory;
    }

    public static Certification of(final String name, final Authority  authority, final Category category, final Category subCategory){
        return new Certification(name, authority, category, subCategory);
    }

     public void incrementViewCount() {
        this.viewCount++;
    }
}
