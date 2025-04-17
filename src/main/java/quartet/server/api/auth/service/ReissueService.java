package quartet.server.api.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quartet.server.core.security.jwt.JwtUtil;
import quartet.server.domain.auth.exception.RefreshTokenException;
import quartet.server.domain.auth.model.RefreshToken;
import quartet.server.domain.auth.repository.RefreshTokenRepository;
import quartet.server.domain.member.exception.MemberNotFoundException;
import quartet.server.domain.member.model.Member;
import quartet.server.domain.member.repository.MemberRepository;

import java.util.Arrays;

import static quartet.server.core.code.AuthErrorCode.REFRESH_TOKEN_INVALID;

@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();
        refreshToken = Arrays.stream(cookies).filter((cookie) -> cookie.getName().equals("refreshToken"))
                .findFirst().get().getValue();

        // refreshToken 유효 여부 확인
        jwtUtil.validateRefreshToken(refreshToken);

        Long memberId = jwtUtil.getMemberId(refreshToken);

        // refresh DB 조회
        Boolean isExist = refreshTokenRepository.existsByToken(refreshToken);

        // DB 에 없는 리프레시 토큰
        if(!isExist) {
            throw new RefreshTokenException(REFRESH_TOKEN_INVALID);
        }

        // 새로운 accessToken 생성, 저장
        String newAccessToken = jwtUtil.generateAccessToken(memberId);

        // 기존 refreshToken DB 삭제, 새로운 refreshToken 생성, 저장
        refreshTokenRepository.deleteByToken(refreshToken);
        refreshToken = jwtUtil.generateRefreshToken(memberId);

        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        RefreshToken newRefreshToken = RefreshToken.of(member, refreshToken);
        refreshTokenRepository.save(newRefreshToken);

        response.setHeader("accessToken", newAccessToken);

        addCookie(response, "refreshToken", refreshToken, 60 * 60 * 24);
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAgeSeconds) {
        String cookie = name + "=" + value +
                "; Path=/" +
                "; Max-Age=" + maxAgeSeconds +
                "; HttpOnly" +
                "; Secure" +
                "; SameSite=None" +
                "; Domain=license-hunter.vercel.app";

        response.addHeader("Set-Cookie", cookie);
    }
}
