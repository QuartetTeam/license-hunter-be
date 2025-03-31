package quartet.server.domain.certification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.IdentifiableEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "certification_qualification")
public class CertificationQualification extends IdentifiableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    @Comment("자격증 id")
    private Certification certification;

    @Column(nullable = false, length = 255)
    @Comment("응시자격")
    private String qualification;

    @Column(length = 255)
    @Comment("자격 유형")
    private String type;

    private CertificationQualification(final Certification certification, final String qualification, final String type) {
        this.certification = certification;
        this.qualification = qualification;
        this.type = type;
    }

    public static CertificationQualification of(final Certification certification, final String qualification, final String type) {
        return new CertificationQualification(certification, qualification, type);
    }
} 