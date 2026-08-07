package com.hennie.springdatajpa.crawler.service;

import com.hennie.springdatajpa.crawler.model.CrawledArticle;
import com.hennie.springdatajpa.domain.enterprise.entity.Enterprise;
import com.hennie.springdatajpa.domain.enterprise.entity.TechArticleCrawlSource;
import com.hennie.springdatajpa.domain.enterprise.repository.EnterpriseRepository;
import com.hennie.springdatajpa.domain.techarticle.entity.TechArticle;
import com.hennie.springdatajpa.domain.techarticle.repository.TechArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TechArticleImportService {

    private static final ZoneId ARTICLE_TIME_ZONE = ZoneId.of("Asia/Seoul");

    private final TechArticleRepository techArticleRepository;
    private final EnterpriseRepository enterpriseRepository;

    @Transactional
    public ImportResult importArticles(List<CrawledArticle> articles, boolean dryRun) {
        if (articles.isEmpty()) {
            return new ImportResult(0, 0, 0);
        }

        List<String> originalUrls = articles.stream()
                .map(CrawledArticle::originalUrl)
                .distinct()
                .toList();
        Map<String, TechArticle> existingByUrl = techArticleRepository
                .findAllByOriginalUrlIn(originalUrls).stream()
                .collect(Collectors.toMap(
                        TechArticle::getOriginalUrl,
                        Function.identity(),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
        Map<TechArticleCrawlSource, Enterprise> enterpriseByCrawlSource =
                loadEnterprises(articles);

        int inserted = 0;
        int updated = 0;
        int skipped = 0;
        LocalDateTime crawledAt = LocalDateTime.now(ARTICLE_TIME_ZONE);
        List<TechArticle> changedArticles = new ArrayList<>();

        for (CrawledArticle article : articles) {
            LocalDateTime publishedAt = toLocalDateTime(article.publishedAt());
            TechArticle existing = existingByUrl.get(article.originalUrl());
            if (existing == null) {
                TechArticle created = create(
                        enterpriseByCrawlSource.get(article.source()),
                        article,
                        publishedAt,
                        crawledAt
                );
                existingByUrl.put(article.originalUrl(), created);
                inserted++;
                if (!dryRun) {
                    changedArticles.add(created);
                }
                continue;
            }

            if (!existing.hasMetadataChanges(
                    article.title(),
                    publishedAt
            )) {
                skipped++;
                continue;
            }

            updated++;
            if (!dryRun) {
                existing.updateMetadata(
                        article.title(),
                        publishedAt,
                        crawledAt
                );
                changedArticles.add(existing);
            }
        }

        if (!dryRun && !changedArticles.isEmpty()) {
            techArticleRepository.saveAll(changedArticles);
        }
        return new ImportResult(inserted, updated, skipped);
    }

    private TechArticle create(
            Enterprise enterprise,
            CrawledArticle article,
            LocalDateTime publishedAt,
            LocalDateTime crawledAt
    ) {
        return new TechArticle(
                enterprise,
                article.title(),
                article.originalUrl(),
                publishedAt,
                crawledAt
        );
    }

    private Map<TechArticleCrawlSource, Enterprise> loadEnterprises(
            List<CrawledArticle> articles
    ) {
        List<TechArticleCrawlSource> crawlSources = articles.stream()
                .map(CrawledArticle::source)
                .distinct()
                .toList();
        Map<TechArticleCrawlSource, Enterprise> enterprises = enterpriseRepository
                .findAllByCrawlSourceIn(crawlSources).stream()
                .collect(Collectors.toMap(
                        Enterprise::getCrawlSource,
                        Function.identity()
                ));

        for (TechArticleCrawlSource crawlSource : crawlSources) {
            Enterprise enterprise = enterprises.get(crawlSource);
            if (enterprise == null) {
                throw new IllegalStateException(
                        "ENTERPRISE_NOT_FOUND: " + crawlSource
                );
            }
            if (!enterprise.isStatus()) {
                throw new IllegalStateException(
                        "ENTERPRISE_INACTIVE: " + crawlSource
                );
            }
        }
        return enterprises;
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ARTICLE_TIME_ZONE);
    }

    public record ImportResult(int inserted, int updated, int skipped) {
    }
}
