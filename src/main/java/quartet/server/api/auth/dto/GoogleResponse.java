package quartet.server.api.auth.dto;

import java.util.Map;

public class GoogleResponse implements OAuth2Response {

    private final Map<String, Object> attributes;

    public GoogleResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getNickname() {
        String email = attributes.get("email").toString();
        return email.substring(0, email.indexOf("@"));
    }

    @Override
    public String getProfileImageUrl() {
        return attributes.get("picture").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }
}
