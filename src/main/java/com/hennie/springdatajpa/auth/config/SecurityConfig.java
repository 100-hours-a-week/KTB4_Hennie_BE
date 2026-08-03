package com.hennie.springdatajpa.auth.config;

import com.hennie.springdatajpa.auth.jwt.JwtAuthenticationFilter;
import com.hennie.springdatajpa.auth.support.SecurityErrorResponseWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityErrorResponseWriter securityErrorResponseWriter;
    private final Environment environment;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/users/signup",
            "/users/login",
            "/users/token/refresh",
    };

    private static final String[] H2_CONSOLE_ENDPOINTS = {
            "/h2-console",
            "/h2-console/**",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers(HttpMethod.GET, "/posts").permitAll() // 게시글 목록/상세 조회는 비로그인 없이도 가능 (단, 하위 세부 기능들은 로그인이 필요함)
                            .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, "^/posts/\\d+$")).permitAll()
                            .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                            .requestMatchers(PUBLIC_ENDPOINTS).permitAll();
                    if (h2ConsoleEnabled()) {
                        auth.requestMatchers(H2_CONSOLE_ENDPOINTS).permitAll();
                    }
                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(exception -> exception // 인증이 없거나 실패하면, 401 응답
                        .authenticationEntryPoint((request, response, authException) ->
                                securityErrorResponseWriter.writeUnauthorized(response)
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                securityErrorResponseWriter.writeForbidden(response)
                        )
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    private boolean h2ConsoleEnabled() {
        return environment.getProperty("spring.h2.console.enabled", Boolean.class, false);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
