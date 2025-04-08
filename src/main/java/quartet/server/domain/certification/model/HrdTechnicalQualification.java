package quartet.server.domain.certification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.domain.certification.type.TechnicalGradeType;

@Entity
@Table(name = "hrd_technical_qualification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HrdTechnicalQualification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    @Comment("응시자격")
    private String qualification;

    @Column(length = 255)
    @Comment("자격 유형")
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("기술자격 등급(국가기술자격만 있음)")
    private TechnicalGradeType grade;

    public HrdTechnicalQualification(String qualification, String type, TechnicalGradeType grade) {
        this.qualification = qualification;
        this.type = type;
        this.grade = grade;
    }
} 