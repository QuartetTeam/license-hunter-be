package quartet.server.entity.certification;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.IdentifiableEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "certification")
public class Certification extends IdentifiableEntity {
    @Column(nullable = false, length = 100)
    @Comment("자격증명")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="authority_id")
    @Comment("시행 기관")
    private Authority authority;

    //TODO: 카테고리 엔티티 생성 후, 참조 관계로 변경해야함
    private long categoryId;

    //TODO: 카테고리 엔티티 생성 후, 참조 관계로 변경해야함
    // 외부 클래스에서 엔티티 직접 생성을 막습니다.
    private Certification(String name, Authority  authority, long categoryId){
        this.name = name;
        this.authority = authority;
        this.categoryId = categoryId;
    }

     // of()를 통해서 mapper가 엔티티를 생성할 수 있도록 합니다.
    public static Certification of(String name, Authority  authority, long categoryId){
        // 이후 엔티티를 생성과 관련된 비즈니스 로직이 생기는 경우 이곳에 추가합니다.
        return new Certification(name, authority, categoryId);
    }
}
