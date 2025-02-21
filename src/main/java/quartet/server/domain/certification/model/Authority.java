package quartet.server.domain.certification.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.IdentifiableEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "authority")
public class Authority extends IdentifiableEntity {
    @Column(nullable = false, length = 100)
    @Comment("시행기관 이름")
    private String name;

    @Column(nullable = false, length = 100)
    @Comment("시행기관 홈페이지")
    private String websiteUrl;

    @Column(nullable = false, length = 100)
    @Comment("접수신청 페이지")
    private String applicationUrl;

    @Column(length = 100)
    @Comment("시행기관 아이콘 이미지")
    private String iconImageUrl;

    private Authority(String name, String websiteUrl, String applicationUrl, String iconImageUrl) {
        this.name = name;
        this.websiteUrl = websiteUrl;
        this.applicationUrl = applicationUrl;
        this.iconImageUrl = iconImageUrl;
    }

    public static Authority of(String name, String websiteUrl, String applicationUrl, String iconImageUrl){
        return new Authority(name, websiteUrl, applicationUrl, iconImageUrl);
    }
}
