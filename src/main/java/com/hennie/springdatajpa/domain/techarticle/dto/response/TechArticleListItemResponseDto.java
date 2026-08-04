package com.hennie.springdatajpa.domain.techarticle.dto.response;

import com.hennie.springdatajpa.domain.techarticle.entity.TechArticle;
import com.hennie.springdatajpa.domain.techarticle.entity.TechArticleSource;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TechArticleListItemResponseDto {

    private final Long articleId;
    private final String title;
    private final TechArticleSource enterprise;
    private final String originalUrl;
    private final LocalDateTime publishedAt;

    public TechArticleListItemResponseDto(TechArticle article) {
        this.articleId = article.getId();
        this.title = article.getTitle();
        this.enterprise = article.getEnterprise();
        this.originalUrl = article.getOriginalUrl();
        this.publishedAt = article.getPublishedAt();
    }
}
