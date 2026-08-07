package com.hennie.springdatajpa.domain.techarticle.dto.response;

import com.hennie.springdatajpa.domain.enterprise.entity.TechArticleCrawlSource;
import com.hennie.springdatajpa.domain.techarticle.entity.TechArticle;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TechArticleListItemResponseDto {

    private final Long articleId;
    private final String title;
    private final TechArticleCrawlSource enterprise;
    private final Long enterpriseId;
    private final String enterpriseName;
    private final String originalUrl;
    private final LocalDateTime publishedAt;

    public TechArticleListItemResponseDto(TechArticle article) {
        this.articleId = article.getId();
        this.title = article.getTitle();
        this.enterprise = article.getEnterprise().getCrawlSource();
        this.enterpriseId = article.getEnterprise().getId();
        this.enterpriseName = article.getEnterprise().getName();
        this.originalUrl = article.getOriginalUrl();
        this.publishedAt = article.getPublishedAt();
    }
}
