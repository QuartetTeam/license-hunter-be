package quartet.server.domain.mail.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.BaseAuditEntity;
import quartet.server.domain.certification.model.Certification;
import quartet.server.domain.member.model.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mailing",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_mailing_member_certification",
                        columnNames = {"member_id", "certification_id"}
                )
        }
)
public class Mailing extends BaseAuditEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("알림을 구독한 사용자")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    @Comment("알림 대상 자격증")
    private Certification certification;

    public Mailing(final Member member, final Certification certification) {
        this.member = member;
        this.certification = certification;
    }
    public static Mailing of(final Member member, final Certification certification) {
        return new Mailing(member, certification);
    }
}
