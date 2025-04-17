package quartet.server.core.security.ouath2;

import jakarta.servlet.ServletException;
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

        addCookie(request, response, "accessToken", accessToken, 60 * 10);
        addCookie(request, response, "refreshToken", refreshToken, 60 * 60 * 24);

        // 로컬 환경에서 테스트 시 localhost로 리다이렉트, 운영 환경에서는 실제 도메인
        String redirectUrl = request.getRequestURL().toString().contains("localhost")
                ? "http://localhost:5173/api/v1/oauth2-jwt-header"
                : "https://license-hunter.vercel.app/api/v1/oauth2-jwt-header";
        response.sendRedirect(redirectUrl);
    }

    private void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAgeSeconds) {
        String domain = request.getRequestURL().toString().contains("localhost") ? "localhost" : "license-hunter.vercel.app";
        String secure = request.getRequestURL().toString().contains("localhost") ? "" : "; Secure"; // 로컬 환경에서는 Secure를 제외
        String cookie = name + "=" + value +
                "; Path=/" +
                "; Max-Age=" + maxAgeSeconds +
                "; HttpOnly" +
                secure +
                "; SameSite=None" +
                "; Domain=" + domain;

        response.addHeader("Set-Cookie", cookie);
    }
//        addCookie(response, "accessToken", accessToken, 60 * 10);
//        addCookie(response, "refreshToken", refreshToken, 60 * 60 * 24);


//        response.sendRedirect("https://license-hunter.vercel.app/api/v1/oauth2-jwt-header");
//    }
//
//    private void addCookie(HttpServletResponse response, String name, String value, int maxAgeSeconds) {
//        String cookie = name + "=" + value +
//                "; Path=/" +
//                "; Max-Age=" + maxAgeSeconds +
//                "; HttpOnly" +
//                "; Secure" +
//                "; SameSite=None" +
//                "; Domain=license-hunter.vercel.app";
//
//        response.addHeader("Set-Cookie", cookie);
//    }
}
