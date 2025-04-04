package quartet.server.domain.auth.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.BaseTimeEntity;
import quartet.server.domain.member.model.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_token")
public class RefreshToken extends BaseTimeEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("회원 ID")
    private Member member;

    @Column(name = "token", nullable = false, length = 512)
    @Comment("리프레시 토큰 값")
    private String token;

    private RefreshToken(final Member member, final String token) {
        this.member = member;
        this.token = token;
    }

    public static RefreshToken of(final Member member, final String token) {
        return new RefreshToken(member, token);
    }
}
