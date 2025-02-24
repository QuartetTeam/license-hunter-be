package quartet.server.domain.certification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import quartet.server.core.entity.IdentifiableEntity;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "certification_view_log")
public class CertificationViewLog extends IdentifiableEntity {

    //@Todo 최지희: user 엔티티 생성된 후, 참조 관계 설정
    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "user_id", nullable = false)
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    private Certification certification;

    @CreatedDate
    @Column(name = "created_at",nullable = false, updatable = false)
    private Instant createdAt;

    private CertificationViewLog(final Integer userId, final Certification certification) {
        this.userId = userId;
        this.certification = certification;
    }

    public static CertificationViewLog of(final Integer userId, final Certification certification) {
        return new CertificationViewLog(userId, certification);
    }
}
