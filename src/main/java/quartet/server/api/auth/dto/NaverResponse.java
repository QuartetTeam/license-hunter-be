package quartet.server.api.auth.dto;

import java.util.Map;

public class NaverResponse implements OAuth2Response {

    private final Map<String, Object> attributes;

    public NaverResponse(Map<String, Object> attribute) {
        this.attributes = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getNickname() {
        String email = attributes.get("email").toString();
        return email.substring(0, email.indexOf("@"));
    }

    @Override
    public String getProfileImageUrl() {
        return attributes.get("profile_image").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }
}
