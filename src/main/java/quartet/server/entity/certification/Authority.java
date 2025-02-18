package quartet.server.entity.certification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.IdentifiableEntity;

import java.math.BigDecimal;

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

    // 외부 클래스에서 엔티티 직접 생성을 막습니다.
    private Authority(String name, String websiteUrl, String applicationUrl, String iconImageUrl) {
        this.name = name;
        this.websiteUrl = websiteUrl;
        this.applicationUrl = applicationUrl;
        this.iconImageUrl = iconImageUrl;
    }

    // of()를 통해서 mapper가 엔티티를 생성할 수 있도록 합니다.
    public static Authority of(String name, String websiteUrl, String applicationUrl, String iconImageUrl){
        // 이후 엔티티를 생성과 관련된 비즈니스 로직이 생기는 경우 이곳에 추가합니다.
        return new Authority(name, websiteUrl, applicationUrl, iconImageUrl);
    }
}
