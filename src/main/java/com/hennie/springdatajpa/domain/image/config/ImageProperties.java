package com.hennie.springdatajpa.domain.image.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "image")
public class ImageProperties {
    private String uploadDirectory = "./uploads";
    private String baseUrl = "http://localhost:8080/uploads";
}
