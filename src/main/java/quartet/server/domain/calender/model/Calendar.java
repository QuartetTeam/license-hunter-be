package quartet.server.domain.calender.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.BaseAuditEntity;
import quartet.server.domain.certification.model.Certification;

@Entity
@Table(name = "calendar")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Calendar extends BaseAuditEntity { // todo: User 엔티티 생성후 주석 해제 예정
//    @ManyToOne(fetch = FetchType.LAZY) //
//    @JoinColumn(name = "user_id", nullable = false)
//    @Comment("캘린더를 구독한 사용자")
//    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    @Comment("구독한 자격증")
    private Certification certification;
}