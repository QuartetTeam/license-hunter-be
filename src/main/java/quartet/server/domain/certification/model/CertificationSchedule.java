package quartet.server.domain.certification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.IdentifiableEntity;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ScheduleType;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "certification_schedule")
public class CertificationSchedule extends IdentifiableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id",  nullable = false)
    @Comment("자격증 id")
    private Certification certification;

    @Column(nullable = true)
    @Comment("시험 종류")
    @Enumerated(EnumType.STRING)
    private ExamType examType;

    @Column(nullable = false)
    @Comment("일정 종류")
    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    @Column(nullable = false)
    @Comment("일자")
    private Instant date;

    @Column(nullable = true, length = 255)
    @Comment("시험 회차")
    private String examRound;

    private CertificationSchedule(final Certification certification, final ExamType examType, 
                                final ScheduleType scheduleType, final Instant date, final String examRound) {
        this.certification = certification;
        this.examType = examType;
        this.scheduleType = scheduleType;
        this.date = date;
        this.examRound = examRound;
    }

    public static CertificationSchedule of(final Certification certification, final ExamType examType, 
                                         final ScheduleType scheduleType, final Instant date, final String examRound) {
        return new CertificationSchedule(certification, examType, scheduleType, date, examRound);
    }
}
