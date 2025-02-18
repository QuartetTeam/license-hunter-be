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
    String name;

    @Column(nullable = false, length = 100)
    @Comment("시행기관 홈페이지")
    String website_url;

    @Column(nullable = false, length = 100)
    @Comment("접수신청 페이지")
    String application_url;

    @Column(length = 100)
    @Comment("시행기관 아이콘 이미지")
    String icon_image_url;

    // 외부 클래스에서 엔티티 직접 생성을 막습니다.
    private Authority(String name, String website_url, String application_url, String icon_image_url) {
        this.name = name;
        this.website_url = website_url;
        this.application_url = application_url;
        this.icon_image_url = icon_image_url;
    }

    // of()를 통해서 mapper가 엔티티를 생성할 수 있도록 합니다.
    public static Authority of(String name, String website_url, String application_url, String icon_image_url){
        // 이후 엔티티를 생성과 관련된 비즈니스 로직이 생기는 경우 이곳에 추가합니다.
        return new Authority(name, website_url, application_url, icon_image_url);
    }
}
