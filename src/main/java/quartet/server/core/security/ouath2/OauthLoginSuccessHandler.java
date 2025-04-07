package quartet.server.core.security.ouath2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import quartet.server.api.auth.dto.CustomOAuth2User;
import quartet.server.core.security.jwt.JwtUtil;
import quartet.server.domain.auth.model.RefreshToken;
import quartet.server.domain.auth.repository.RefreshTokenRepository;
import quartet.server.domain.member.exception.MemberNotFoundException;
import quartet.server.domain.member.model.Member;
import quartet.server.domain.member.repository.MemberRepository;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OauthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        Long memberId = customOAuth2User.getMemberId();

        String accessToken = jwtUtil.generateAccessToken(memberId);
        String refreshToken = jwtUtil.generateRefreshToken(memberId);

        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        refreshTokenRepository.deleteByMemberId(memberId);
        RefreshToken newRefreshToken = RefreshToken.of(member, refreshToken);
        refreshTokenRepository.save(newRefreshToken);

        response.addCookie(createCookie("accessToken", accessToken, 60 * 10));
        response.addCookie(createCookie("refreshToken", refreshToken, 24 * 60 * 60));

        response.sendRedirect("/api/v1/oauth2-jwt-header");
    }

    public static Cookie createCookie(String key, String value, Integer expiredS) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(expiredS);
        return cookie;
    }
}