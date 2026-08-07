package com.hennie.springdatajpa.domain.enterprise.entity;

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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "enterprise",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_enterprise_crawl_source",
                columnNames = "crawl_source"
        )
)
@Getter
public class Enterprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enterprise_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "crawl_source", nullable = false, length = 30)
    private TechArticleCrawlSource crawlSource;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private boolean status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    protected Enterprise() {
    }

    public Enterprise(TechArticleCrawlSource crawlSource, String name) {
        this.crawlSource = crawlSource;
        this.name = name;
        this.status = true;
    }

    public void activate() {
        this.status = true;
    }

    public void deactivate() {
        this.status = false;
    }

    public boolean canSubscribe() {
        return status;
    }
}
