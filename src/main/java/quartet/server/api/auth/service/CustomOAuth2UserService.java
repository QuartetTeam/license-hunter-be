package quartet.server.api.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import quartet.server.api.auth.dto.*;
import quartet.server.domain.image.service.ImageService;
import quartet.server.domain.mail.type.MailingStatus;
import quartet.server.domain.member.model.Member;
import quartet.server.domain.member.repository.MemberRepository;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final ImageService imageService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }else if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String socialId = oAuth2Response.getProviderId();
        String socialProvider = oAuth2Response.getProvider();
        String email = oAuth2Response.getEmail();
        String nickname = oAuth2Response.getNickname();
        String socialProfileImageUrl = oAuth2Response.getProfileImageUrl();

        Member existMember = memberRepository.findBySocialId(socialId);
        Long memberId = null;

        if (existMember == null) {
            String profileImageUrl = null;
            if (socialProfileImageUrl != null && !socialProfileImageUrl.isEmpty()) {
                profileImageUrl = imageService.uploadImageFromUrl(socialProfileImageUrl);
            }

            Member newMember = Member.of(socialId, socialProvider, email, nickname, profileImageUrl, MailingStatus.ACTIVE);
            memberRepository.save(newMember);
            memberId = newMember.getId();
        } else {
            memberId = existMember.getId();
        }

        return new CustomOAuth2User(memberId);
    }
}