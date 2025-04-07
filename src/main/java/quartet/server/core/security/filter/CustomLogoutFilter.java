package quartet.server.core.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import quartet.server.domain.auth.repository.RefreshTokenRepository;

import java.io.IOException;
import java.util.Arrays;

/**
 * 로그아웃 필터
 * refresh 토큰 만료
 */
@Component
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {
    private final RefreshTokenRepository refreshRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        // uri check(요청 경로가 /logout인지 확인
        if (!requestURI.matches("^\\/logout$")) {
            chain.doFilter(request, response);
            return;
        }
        // method check(POST 방식 요청인지 확인)
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            chain.doFilter(request, response);
            return;
        }

        String refreshToken = null;
        Cookie[] cookies = request.getCookies();

        refreshToken = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("refreshToken"))
                .findFirst().get().getValue();

        Boolean isExist = refreshRepository.existsByToken(refreshToken);

        // DB에서 refreshToken 삭제
        if(isExist){
            refreshRepository.deleteByToken(refreshToken);
        }

        Cookie cookie = createCookie("refreshToken", null, 0);
        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    public static Cookie createCookie(String key, String value, Integer expiredS) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(expiredS);
        return cookie;
    }
}