package quartet.server.domain.member.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.BaseTimeEntity;
import quartet.server.domain.category.model.Category;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_category")
public class MemberCategory extends BaseTimeEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("회원 ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @Comment("카테고리 ID")
    private Category category;

    private MemberCategory(final Member member, final Category category) {
        this.member = member;
        this.category = category;
    }

    public static MemberCategory of(final Member member, final Category category) {
        return new MemberCategory(member, category);
    }
}