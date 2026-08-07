package com.hennie.springdatajpa.crawler.model;

import com.hennie.springdatajpa.domain.enterprise.entity.TechArticleCrawlSource;

import java.time.Instant;

public record CrawledArticle(
        TechArticleCrawlSource source,
        String title,
        String originalUrl,
        Instant publishedAt
) { }
