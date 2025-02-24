package quartet.server.domain.certification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.IdentifiableEntity;
import quartet.server.domain.certification.type.ExamType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "certification_pass_criteria")
public class CertificationPassCriteria extends IdentifiableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    @Comment("자격증 id")
    private Certification certification;

    @Column(nullable = false)
    @Comment("시험 종류")
    private ExamType examType;

    @Lob
    @Comment("합격 기준")
    private String criteria;

    private CertificationPassCriteria(final Certification certification, final ExamType examType, final String criteria) {
        this.certification = certification;
        this.examType = examType;
        this.criteria = criteria;
    }

     public static CertificationPassCriteria of(final Certification certification, final ExamType examType, final String criteria) {
        return new CertificationPassCriteria(certification, examType, criteria);
    }
}
