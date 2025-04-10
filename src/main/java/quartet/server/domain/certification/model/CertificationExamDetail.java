package quartet.server.domain.certification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.IdentifiableEntity;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ProblemType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="certification_exam_details")
public class CertificationExamDetail extends IdentifiableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    @Comment("자격증 id")
    private Certification certification;

    @Column(nullable = false)
    @Comment("시험 종류")
    @Enumerated(EnumType.STRING)
    private ExamType examType;

    @Column(nullable = false, length = 150)
    @Comment("과목명")
    private String subject;

    @Column(nullable = true)
    @Comment("문제 유형")
    @Enumerated(EnumType.STRING)
    private ProblemType problemType;

    @Column(nullable = true)
    @Comment("총 문제수")
    private String totalProblemsCount;

    @Column(nullable = true)
    @Comment("시험시간")
    private String duration;


    private CertificationExamDetail(final Certification certification, final ExamType examType, final String subject,
                                   final ProblemType problemType, final String totalProblemsCount, final String duration) {
        this.certification = certification;
        this.examType = examType;
        this.subject = subject;
        this.problemType = problemType;
        this.totalProblemsCount = totalProblemsCount;
        this.duration = duration;
    }

    public static CertificationExamDetail of(final Certification certification, final ExamType examType, final String subject,
                                             final ProblemType problemType, final String totalProblemsCount, final String duration) {
        return new CertificationExamDetail(certification, examType, subject, problemType, totalProblemsCount, duration);
    }

}
