package quartet.server.api.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quartet.server.core.security.jwt.JwtUtil;

/**
 * OAuth2 리다이렉트 문제로 access 토큰을 httpOnly 쿠키로 발급
 * -> 프론트에서 바로 재요청하면 해당 access 토큰 헤더에 싣고, 쿠키는 만료시킴
 */
@Service
@RequiredArgsConstructor
public class OAuth2JwtHeaderService {

    private final JwtUtil jwtUtil;

    public void oauth2JwtHeaderSet(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String accessToken = null;


        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("accessToken")){
                accessToken = cookie.getValue();
            }
        }

        jwtUtil.validateAccessToken(accessToken);

        // 클라이언트의 access 토큰 쿠키를 만료
        addCookie(response, "accessToken", null, 0);
        response.addHeader("accessToken", accessToken);
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
