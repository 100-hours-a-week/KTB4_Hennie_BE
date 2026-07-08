package com.hennie.springdatajpa.auth.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties { // JWT 설정값 타입 바인딩
    private String secret;
    private long accessTokenExpSeconds;
    private long refreshTokenExpSeconds;
}
