package com.hennie.springdatajpa.domain.techarticle.entity;

import com.hennie.springdatajpa.domain.enterprise.entity.Enterprise;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "tech_article",
        indexes = @Index(
                name = "ix_tech_article_enterprise",
                columnList = "enterprise_id"
        ),
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enterprise_id", nullable = false)
    private Enterprise enterprise;

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
            Enterprise enterprise,
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
