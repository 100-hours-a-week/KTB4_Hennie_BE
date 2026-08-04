package com.hennie.springdatajpa.domain.techarticle.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "tech_article",
        uniqueConstraints = {
                @UniqueConstraint(name = "tech_article_original_url", columnNames = "original_url")
        }
)
@Getter
public class TechArticle {
    public static final int TITLE_MAX_LENGTH = 300;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tech_article_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "enterprise", nullable = false)
    private TechArticleSource enterprise;

    @Column(nullable = false, length = TITLE_MAX_LENGTH)
    private String title;

    @Column(name = "original_url", nullable = false, length = 768)
    private String originalUrl;

    private LocalDateTime publishedAt;

    @Column(nullable = false)
    private LocalDateTime crawledAt;

    protected TechArticle() {
    }

    public TechArticle(
            TechArticleSource enterprise,
            String title,
            String originalUrl,
            LocalDateTime publishedAt,
            LocalDateTime crawledAt
    ) {
        this.enterprise = enterprise;
        this.title = title;
        this.originalUrl = originalUrl;
        this.publishedAt = publishedAt;
        this.crawledAt = crawledAt;
    }

    public boolean hasMetadataChanges(
            String title,
            LocalDateTime publishedAt
    ) {
        return differsWhenPresent(this.title, title)
                || differsWhenPresent(this.publishedAt, publishedAt);
    }

    public void updateMetadata(
            String title,
            LocalDateTime publishedAt,
            LocalDateTime crawledAt
    ) {
        this.title = presentOrExisting(title, this.title);
        this.publishedAt = presentOrExisting(publishedAt, this.publishedAt);
        this.crawledAt = crawledAt;
    }

    private static boolean differsWhenPresent(Object current, Object incoming) {
        return isPresent(incoming) && !Objects.equals(current, incoming);
    }

    private static <T> T presentOrExisting(T incoming, T existing) {
        return isPresent(incoming) ? incoming : existing;
    }

    private static boolean isPresent(Object value) {
        return value != null && (!(value instanceof String text) || !text.isBlank());
    }
}
