package quartet.server.domain.member.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.IdentifiableEntity;
import quartet.server.domain.category.model.MainCategory;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_category")
public class MemberCategory extends IdentifiableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("회원 ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_category_id", nullable = false)
    @Comment("대분류 카테고리 ID")
    private MainCategory mainCategory;

    private MemberCategory(final Member member, final MainCategory mainCategory) {
        this.member = member;
        this.mainCategory = mainCategory;
    }

    public static MemberCategory of(final Member member, final MainCategory mainCategory) {
        return new MemberCategory(member, mainCategory);
    }
}