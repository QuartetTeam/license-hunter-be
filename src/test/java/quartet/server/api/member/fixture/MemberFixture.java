package quartet.server.api.member.fixture;

import quartet.server.domain.member.model.Member;

public class MemberFixture {

    public static Member createMember() {
        return Member.of("testSocialId", "GOOGLE", "test@example.com", "TestUser", "profile.jpg", "Intro");
    }
}