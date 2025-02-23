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
@Table(name = "certification_description")
public class CertificationDescription extends IdentifiableEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    @Comment("자격증 id")
    private Certification certification;

    @Lob
    @Comment("자격증 기본 설명")
    private String description;

    @Lob
    @Comment("응시 자격")
    private String qualification;

    private CertificationDescription(final Certification certification, final String description, final String qualification) {
        this.certification = certification;
        this.description = description;
        this.qualification = qualification;
    }

    public static CertificationDescription of(final Certification certification, final String description, final String qualification) {
        return new CertificationDescription(certification, description, qualification);
    }

}
