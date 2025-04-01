package quartet.server.domain.certification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.IdentifiableEntity;
import quartet.server.domain.certification.type.TechnicalGradeType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "technical_qualification")
public class TechnicalQualification extends IdentifiableEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    @Comment("자격증 id")
    private Certification certification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    @Comment("기술자격 등급(국가기술자격만 있음)")
    private TechnicalGradeType grade;

    @Column(length = 10, nullable = false)
    @Comment("종목코드")
    private String jmcd;

    private TechnicalQualification(final Certification certification, final TechnicalGradeType grade, final String jmcd) {
        this.certification = certification;
        this.grade = grade;
        this.jmcd = jmcd;
    }

    public static TechnicalQualification of(final Certification certification, final TechnicalGradeType grade, final String jmcd) {
        return new TechnicalQualification(certification, grade, jmcd);
    }
} 