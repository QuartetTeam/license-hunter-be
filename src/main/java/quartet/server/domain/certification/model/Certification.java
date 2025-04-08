package quartet.server.domain.certification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.IdentifiableEntity;
import quartet.server.domain.category.model.SubCategory;
import quartet.server.domain.certification.type.QualificationType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "certification")
public class Certification extends IdentifiableEntity {
    @Column(nullable = false, length = 100)
    @Comment("자격증명")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="authority_id", nullable = false)
    @Comment("시행 기관")
    private Authority authority;

    @OneToOne(mappedBy = "certification", cascade = CascadeType.ALL, orphanRemoval = true)
    private CertificationDescription description;

    @OneToMany(mappedBy = "certification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CertificationSchedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "certification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CertificationExamDetail> examDetails = new ArrayList<>();

    @OneToMany(mappedBy = "certification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CertificationPassCriteria> passCriteria = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id", nullable = false)
    @Comment("자격증 카테고리(소분류 기준)")
    private SubCategory subCategory;

    @Column(nullable = false)
    @Comment("상세 페이지 조회 수")
    @ColumnDefault("0")
    private int viewCount;

    @Column(nullable = false)
    @Comment("자격증 유형")
    @Enumerated(EnumType.STRING)
    private QualificationType qualificationType;

    private Certification(final String name, final Authority  authority, final QualificationType qualificationType){
        this.name = name;
        this.authority = authority;
        this.qualificationType = qualificationType;
    }

    public static Certification of(final String name, final Authority  authority, final QualificationType qualificationType){
        return new Certification(name, authority, qualificationType);
    }

    public void incrementViewCount() {
        this.viewCount++;
    }
}
