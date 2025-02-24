package quartet.server.domain.certification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.IdentifiableEntity;

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

    //TODO 최지희: 카테고리 엔티티 생성되면, 참조 관계로 변경해야함
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="category_id",  nullable = false)
//    @Comment("자격증 카테고리 대분류")
    private long categoryId;

    //TODO 최지희: 카테고리 엔티티 생성되면, 참조 관계로 변경해야함
    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name="sub_category_id",  nullable = false)
    //@Comment("자격증 카테고리 소분류")
    private long subCategoryId;

    @Column(nullable = false)
    @Comment("상세 페이지 조회 수")
    private int viewCount = 0;


    //TODO 최지희: 카테고리 엔티티 생성 후, 참조 관계로 변경해야함
    private Certification(final String name, final Authority  authority, final long categoryId, final long subCategoryId){
        this.name = name;
        this.authority = authority;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
    }

    public static Certification of(final String name, final Authority  authority, final long categoryId, final long subCategoryId){
        return new Certification(name, authority, categoryId, subCategoryId);
    }

     public void incrementViewCount() {
        this.viewCount++;
    }
}
