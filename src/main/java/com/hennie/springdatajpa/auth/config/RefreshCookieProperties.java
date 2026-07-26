package com.hennie.springdatajpa.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "refresh-cookie")
public class RefreshCookieProperties {
    private boolean secure = false;
    private String sameSite = "Lax";
    private long maxAgeSeconds = 14L * 24 * 60 * 60;
}
