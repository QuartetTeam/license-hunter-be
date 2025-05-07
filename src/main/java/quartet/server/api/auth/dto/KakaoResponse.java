package quartet.server.api.auth.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attributes;

    public KakaoResponse(Map<String, Object> attribites) {
        this.attributes = attribites;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getNickname() {
        return ((Map) attributes.get("properties")).get("nickname").toString();
    }

    @Override
    public String getProfileImageUrl() {
        return ((Map) attributes.get("properties")).get("profile_image").toString();
    }

    @Override
    public String getEmail() {
        return ((Map) attributes.get("kakao_account")).get("email").toString();
    }
}