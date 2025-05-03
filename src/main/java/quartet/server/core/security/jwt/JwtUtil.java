package quartet.server.core.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import quartet.server.core.code.AuthErrorCode;
import quartet.server.domain.auth.exception.AccessTokenException;
import quartet.server.domain.auth.exception.RefreshTokenException;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenExpiration = accessTokenExpiration * 1000;
        this.refreshTokenExpiration = refreshTokenExpiration * 1000;
    }

    public String generateAccessToken(Long memberId) {
        return Jwts.builder()
                .claim("memberId", memberId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(Long memberId) {
        return Jwts.builder()
                .claim("memberId", memberId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    public Long getMemberId(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("memberId", Long.class);
    }

    public void validateAccessToken(String accessToken) {
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        validateToken(accessToken, false);
    }

    public void validateRefreshToken(final String refreshToken) {
        validateToken(refreshToken, true);
    }

    public void validateToken(final String token, final boolean isRefreshToken) {
        if (token == null) {
            throw isRefreshToken
                    ? new RefreshTokenException(AuthErrorCode.REFRESH_TOKEN_INVALID)
                    : new AccessTokenException(AuthErrorCode.ACCESS_TOKEN_INVALID);
        }

        try {
            parseToken(token);
        } catch (ExpiredJwtException e) {
            System.out.println("ExpiredJwtException: " + e.getMessage());
            throw isRefreshToken
                    ? new RefreshTokenException(AuthErrorCode.REFRESH_TOKEN_EXPIRED)
                    : new AccessTokenException(AuthErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (JwtException e) {
            System.out.println("JwtException: " + e.getMessage()); // 디버깅용 로그 추가
            throw isRefreshToken
                    ? new RefreshTokenException(AuthErrorCode.REFRESH_TOKEN_INVALID)
                    : new AccessTokenException(AuthErrorCode.ACCESS_TOKEN_INVALID);
        }
    }

    private Jws<Claims> parseToken(final String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // JJWT 12.3에서 서명 검증
                .build()
                .parseSignedClaims(token);
    }
}
