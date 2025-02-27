package quartet.server.domain.calender.model;

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
@Table(name = "calendar")
public class Calendar extends BaseAuditEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memeber_id", nullable = false)
    @Comment("캘린더를 구독한 사용자")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    @Comment("구독한 자격증")
    private Certification certification;
}