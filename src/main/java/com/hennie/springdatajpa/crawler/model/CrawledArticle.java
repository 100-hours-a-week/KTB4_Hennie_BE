package com.hennie.springdatajpa.crawler.model;

import com.hennie.springdatajpa.domain.techarticle.entity.TechArticleSource;

import java.time.Instant;

public record CrawledArticle(
        TechArticleSource source,
        String title,
        String originalUrl,
        Instant publishedAt
) { }
