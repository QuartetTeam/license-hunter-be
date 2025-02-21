package quartet.server.domain.member.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.BaseAuditEntity;
import quartet.server.domain.auth.model.RefreshToken;
import quartet.server.domain.calender.model.Calendar;
import quartet.server.domain.mail.model.MailAlarm;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("회원 ID")
    private Long id;

    @Column(name = "social_id", length = 100, nullable = false, unique = true)
    @Comment("소셜 로그인 아이디")
    private String socialId;

    @Column(name = "social_provider", length = 100, nullable = false)
    @Comment("소셜 로그인 제공자")
    private String socialProvider;

    @Column(name = "email", length = 100, unique = true)
    @Comment("이메일")
    private String email;

    @Column(name = "nickname", length = 25)
    @Comment("닉네임")
    private String nickname;

    @Column(name = "profile_image_url", length = 2083)
    @Comment("프로필 이미지 URL")
    private String profileImageUrl;

    @Column(name = "introduction", length = 1000)
    @Comment("프로필 한 줄 소개")
    private String introduction;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("가입 날짜")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Comment("정보 수정 날짜")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    @Comment("계정 삭제 날짜")
    private LocalDateTime deletedAt;

    // 연관관계 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberCategory> memberCategories = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Calendar> calendars = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MailAlarm> mailAlarms = new ArrayList<>();

    private Member(String socialId, String socialProvider, String email, String nickname,
                   String profileImageUrl, String introduction) {
        this.socialId = socialId;
        this.socialProvider = socialProvider;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.introduction = introduction;
        this.createdAt = LocalDateTime.now();
    }

    public static Member of(String socialId, String socialProvider, String email, String nickname,
                          String profileImageUrl, String introduction) {
        return new Member(socialId, socialProvider, email, nickname, profileImageUrl, introduction);
    }

    // 업데이트 메서드
    public void updateProfile(String nickname, String profileImageUrl, String introduction) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.introduction = introduction;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}

