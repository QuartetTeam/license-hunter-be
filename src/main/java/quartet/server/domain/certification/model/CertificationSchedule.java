package quartet.server.domain.certification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.BaseTimeEntity;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ScheduleType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "certification_schedule")
public class CertificationSchedule extends BaseTimeEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id",  nullable = false)
    @Comment("자격증 id")
    private Certification certification;

    @Column(nullable = false)
    @Comment("시험 종류")
    private ExamType examType;

    @Column(nullable = false)
    @Comment("일정 종류")
    private ScheduleType scheduleType;

    private CertificationSchedule(Certification certification, ExamType examType, ScheduleType scheduleType) {
        this.certification = certification;
        this.examType = examType;
        this.scheduleType = scheduleType;
    }

    public static CertificationSchedule of(Certification certification, ExamType examType, ScheduleType scheduleType) {
        return new CertificationSchedule(certification, examType, scheduleType);
    }

}
