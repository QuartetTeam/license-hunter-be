package quartet.server.api.auth.dto;

public interface OAuth2Response {
    String getProvider();

    String getProviderId();

    String getNickname();

    String getProfileImageUrl();

    String getEmail();
}