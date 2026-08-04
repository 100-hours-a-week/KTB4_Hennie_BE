package com.hennie.springdatajpa.crawler.config;

import com.hennie.springdatajpa.domain.techarticle.entity.TechArticleSource;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "crawler")
public class CrawlerProperties { // 실행할 출처 가져오기

    @NotNull
    private Boolean enabled;

    @NotNull
    private Boolean dryRun;

    @NotNull
    private LocalDate publishedFrom;

    @NotNull
    @Min(1000)
    private Long requestDelayMs;

    @NotNull
    @Min(1000)
    private Integer timeoutMs;

    @NotNull
    @Min(1)
    @Max(2)
    private Integer maxAttempts;

    @NotNull
    @Min(1024)
    private Integer maxResponseBytes;

    @NotBlank
    private String userAgent;

    @NotEmpty
    private List<TechArticleSource> sources;
}
