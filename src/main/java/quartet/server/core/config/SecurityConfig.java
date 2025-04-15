package quartet.server.core.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import quartet.server.api.auth.service.CustomOAuth2UserService;
import quartet.server.core.security.filter.CustomLogoutFilter;
import quartet.server.core.security.filter.JwtAuthenticationFilter;
import quartet.server.core.security.ouath2.OauthLoginSuccessHandler;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OauthLoginSuccessHandler oauthLoginSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomLogoutFilter customLogoutFilter;

    @Value("${CORS_ALLOWED_ORIGINS}")
    private String corsAllowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // cors
                .cors((cors) -> cors.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowedOrigins(Arrays.asList(corsAllowedOrigins.split(",")));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("accessToken"));

                        return configuration;
                    }
                }))
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 기본 로그인 UI 제거
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 제거
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 X
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService)) // OAuth2 로그인 설정
                        .successHandler(oauthLoginSuccessHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/","/actuator/health", "/login", "/logout", "/api/v1/oauth2-jwt-header", "/api/v1/reissue",
                                "/api/v1/certifications/**","/api/v1/calendars/**","/api/v1/mailings/**"
                                ).permitAll() // TODO: 나중에 더 추가
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                .logout(AbstractHttpConfigurer::disable) // 기본 로그아웃 비활성화
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(customLogoutFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}