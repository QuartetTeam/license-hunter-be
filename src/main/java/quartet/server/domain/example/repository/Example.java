package quartet.server.domain.example.repository;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import quartet.server.core.entity.BaseAuditEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "example")
@Entity
public class Example extends BaseAuditEntity { // 추후 삭제 예정
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
