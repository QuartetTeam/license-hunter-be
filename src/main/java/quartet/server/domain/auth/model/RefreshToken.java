package quartet.server.domain.auth.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.BaseTimeEntity;
import quartet.server.domain.member.model.Member;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_token")
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("리프레시 토큰 ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    @Comment("회원 ID")
    private Member member;

    @Column(name = "token", nullable = false, length = 512, unique = true)
    @Comment("리프레시 토큰 값")
    private String token;

    @Column(name = "expired_at", nullable = false)
    @Comment("토큰 만료 시간")
    private Instant expiredAt;

    private RefreshToken(Member member, String token, Instant expiredAt) {
        this.member = member;
        this.token = token;
        this.expiredAt = expiredAt;
    }

    public static RefreshToken of(Member member, String token, Instant expiredAt) {
        return new RefreshToken(member, token, expiredAt);
    }

    public void updateToken(String token, Instant expiredAt) {
        this.token = token;
        this.expiredAt = expiredAt;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiredAt);
    }
}
