package quartet.server.domain.certification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import quartet.server.core.entity.IdentifiableEntity;
import quartet.server.domain.member.model.Member;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "certification_view_log")
public class CertificationViewLog extends IdentifiableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    private Certification certification;

    @CreatedDate
    @Column(name = "created_at",nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private CertificationViewLog(final Member member, final Certification certification) {
        this.member = member;
        this.certification = certification;
    }

    public static CertificationViewLog of(final Member member, final Certification certification) {
        return new CertificationViewLog(member, certification);
    }
}
