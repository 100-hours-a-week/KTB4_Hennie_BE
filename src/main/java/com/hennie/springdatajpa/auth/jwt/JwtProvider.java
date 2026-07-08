package com.hennie.springdatajpa.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtProvider { // JWT 생성/파싱/검증 기능
    private final JwtProperties jwtProperties;
    private Key key;

    @PostConstruct
    public void init() { // yaml 내 비밀키 문자열을 UTF-8 바이트로 변환해서 서명용 키 생성
        this.key = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    private String createToken( // 액세스 토큰과 리프레쉬 토큰 발급하는 로직 담당
            String type,
            Long userId,
            Map<String, Object> claims,
            long expSeconds
    ) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("typ", type)
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expSeconds)))
                .signWith((SecretKey) key, Jwts.SIG.HS256) // 대칭키로 서명을 만든다.
                .compact();
    }

    public String createAccessToken(Long userId, String email, String nickname) { // API 요청에 사용될 액세스 토큰을 발급한다.
        return createToken(
                "access",
                userId,
                Map.of("email", email, "nickname", nickname),
                jwtProperties.getAccessTokenExpSeconds()
        );
    }

    public String createRefreshToken(Long userId) { // 재발급 전용 리프레시 토큰을 발급한다.
        return createToken(
                "refresh",
                userId,
                Map.of(),
                jwtProperties.getRefreshTokenExpSeconds()
        );
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key) // 매 요청마다 key로 서명이 일치하는지 확인
                .build()
                .parseSignedClaims(token);
    }

    public boolean isAccessToken(String token) {
        return "access".equals(parse(token).getPayload().get("typ", String.class));
    }

    public Long getUserId(String token) {
        return Long.valueOf(parse(token).getPayload().getSubject());
    }

    public Long getAccessTokenValidityInMilliseconds() {
        return jwtProperties.getAccessTokenExpSeconds() * 1000;
    }
}
