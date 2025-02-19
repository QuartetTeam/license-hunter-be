package quartet.server.domain.mail.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.BaseAuditEntity;
import quartet.server.domain.certification.model.Certification;

@Entity
@Table(name = "mail_alarm")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MailAlarm extends BaseAuditEntity { // todo: User 엔티티 생성후 주석 해제 예정
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    @Comment("알림을 구독한 사용자")
//    private User user;
//
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    @Comment("알림 대상 자격증")
    private Certification certification;

    @Enumerated(EnumType.STRING)
    @Column(name = "mail_alarm_status", nullable = false)
    @Comment("메일 알림 상태")
    private MailAlarmStatus mailAlarmStatus;
}
