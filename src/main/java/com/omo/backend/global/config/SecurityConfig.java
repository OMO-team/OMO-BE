package com.omo.backend.global.config;

import java.util.List;

import com.omo.backend.global.security.AuthenticationEntryPointImpl;
import com.omo.backend.global.security.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final AuthenticationEntryPointImpl authenticationEntryPoint;

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private List<String> allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Swagger 및 API 문서 관련 URL 배열
    private static final String[] SWAGGER_URLS = {
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**",
    };

    // 인증 없이 접근할 수 있는 공개 API URL 배열
    private static final String[] PUBLIC_API_URLS = {
            "/api/v1/members/signup",
            "/auth/v1/email/**",
            "/auth/v1/login/local",
            "/auth/v1/reissue",
            "/api/v1/terms"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configurationSource(configurationSource()))
                .csrf(AbstractHttpConfigurer::disable)  // csrf 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)  // Basic 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable)   // 로그인 폼 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션 방식 비활성화
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SWAGGER_URLS).permitAll()
                        .requestMatchers(PUBLIC_API_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));  // 모든 HTTP 메서드 허용
        configuration.setAllowedHeaders(List.of("*"));  // 모든 헤더값 허용
        configuration.setExposedHeaders(List.of("Authorization"));  // Authorization 헤더 노출 허용
        configuration.setAllowCredentials(true);  // 자격 증명 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // 모든 url 패턴에 대해서 cors 허용 설정
        return source;
    }
}
