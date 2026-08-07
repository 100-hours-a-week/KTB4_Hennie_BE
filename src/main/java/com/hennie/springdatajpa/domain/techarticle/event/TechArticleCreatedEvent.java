package com.hennie.springdatajpa.domain.techarticle.event;

import com.hennie.springdatajpa.domain.techarticle.entity.TechArticle;

import java.time.Instant;
import java.util.Objects;

public record TechArticleCreatedEvent(
        Long articleId,
        Long enterpriseId,
        Instant occurredAt
) {

    public static TechArticleCreatedEvent from(TechArticle article) {
        return new TechArticleCreatedEvent(
                Objects.requireNonNull(article.getId(), "Saved article ID must not be null"),
                article.getEnterprise().getId(),
                Instant.now()
        );
    }
}
