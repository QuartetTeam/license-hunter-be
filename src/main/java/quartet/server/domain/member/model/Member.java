package quartet.server.domain.member.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import quartet.server.core.entity.BaseAuditEntity;
import quartet.server.domain.auth.model.RefreshToken;
import quartet.server.domain.calender.model.Calendar;
import quartet.server.domain.mail.model.Mailing;
import quartet.server.domain.mail.type.MailingStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseAuditEntity {
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

    @Column(name = "deleted_at")
    @Comment("계정 삭제 날짜")
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "mailing_status", nullable = false)
    @Comment("메일링 상태")
    private MailingStatus mailingStatus;

    // 연관관계 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberCategory> memberCategories = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Calendar> calendars = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mailing> mailings = new ArrayList<>();

    private Member(final String socialId, final String socialProvider, final String email, final String nickname,
                   final String profileImageUrl) {
        this.socialId = socialId;
        this.socialProvider = socialProvider;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.mailingStatus = MailingStatus.PAUSED;
    }

    public static Member of(final String socialId, final String socialProvider, final String email, final String nickname,
                            final String profileImageUrl) {
        return new Member(socialId, socialProvider, email, nickname, profileImageUrl);
    }

    public void updateDeletedAt() {
        this.deletedAt = LocalDateTime.now();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updateMailingStatus(final MailingStatus mailAlarmStatus) {
        this.mailingStatus = mailAlarmStatus;
    }
}