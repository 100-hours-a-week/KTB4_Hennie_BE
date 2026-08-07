package com.hennie.springdatajpa.crawler.service;

import com.hennie.springdatajpa.crawler.client.JsoupHttpClient;
import com.hennie.springdatajpa.crawler.config.CrawlerProperties;
import com.hennie.springdatajpa.crawler.model.CrawlReport;
import com.hennie.springdatajpa.crawler.model.CrawlReport.Failure;
import com.hennie.springdatajpa.crawler.model.CrawlReport.SourceResult;
import com.hennie.springdatajpa.crawler.model.CrawledArticle;
import com.hennie.springdatajpa.crawler.parser.AtomFeedParser;
import com.hennie.springdatajpa.crawler.parser.RssFeedParser;
import com.hennie.springdatajpa.crawler.parser.WoowaHtmlParser;
import com.hennie.springdatajpa.crawler.util.HtmlTextExtractor;
import com.hennie.springdatajpa.crawler.util.UrlCanonicalizer;
import com.hennie.springdatajpa.domain.enterprise.entity.TechArticleCrawlSource;
import com.hennie.springdatajpa.domain.enterprise.repository.EnterpriseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hennie.springdatajpa.domain.techarticle.entity.TechArticle.TITLE_MAX_LENGTH;

@Slf4j
@Service
@RequiredArgsConstructor
public class TechArticleCrawler { // 중앙 관리자 역할

    private static final ZoneId PUBLISHED_AT_ZONE = ZoneId.of("Asia/Seoul");

    private final CrawlerProperties properties;
    private final JsoupHttpClient httpClient;
    private final RssFeedParser rssFeedParser;
    private final AtomFeedParser atomFeedParser;
    private final WoowaHtmlParser woowaHtmlParser;
    private final UrlCanonicalizer urlCanonicalizer;
    private final HtmlTextExtractor htmlTextExtractor;
    private final TechArticleImportService importService;
    private final EnterpriseRepository enterpriseRepository;

    public CrawlReport crawl() {
        Set<String> executionUrls = new HashSet<>();
        List<SourceResult> sourceResults = new ArrayList<>();

        Set<TechArticleCrawlSource> activeCrawlSources = enterpriseRepository.findAll().stream()
                .filter(enterprise -> enterprise.isStatus())
                .map(enterprise -> enterprise.getCrawlSource())
                .collect(Collectors.toSet());

        for (TechArticleCrawlSource source : properties.getSources()) {
            if (!activeCrawlSources.contains(source)) {
                log.info("[CRAWLER] inactive enterprise skipped: {}", source);
                continue;
            }
            SourceResult result = crawlSource(source, executionUrls);
            sourceResults.add(result);
            logSourceResult(result);
        }

        CrawlReport report = new CrawlReport(sourceResults);
        log.info("[CRAWLER] fetched={}, inserted={}, updated={}, skipped={}, failed={}",
                report.totalFetched(),
                report.totalInserted(),
                report.totalUpdated(),
                report.totalSkipped(),
                report.totalFailed());
        report.failures().forEach(failure -> log.warn(
                "[CRAWLER_FAILURE] source={}, url={}, reason={}",
                failure.source(), failure.url(), failure.reason()
        ));
        return report;
    }

    private SourceResult crawlSource(TechArticleCrawlSource source, Set<String> executionUrls) {
        Instant startedAt = Instant.now();
        List<Failure> failures = new ArrayList<>();
        int fetched = 0;
        int skipped = 0;
        TechArticleImportService.ImportResult importResult =
                new TechArticleImportService.ImportResult(0, 0, 0);

        try {
            List<CrawledArticle> fetchedArticles = fetch(source);
            fetched = fetchedArticles.size();
            List<CrawledArticle> importableArticles = new ArrayList<>();

            for (CrawledArticle fetchedArticle : fetchedArticles) {
                if (!isPublishedFromConfiguredDate(fetchedArticle)) {
                    skipped++;
                    continue;
                }

                CrawledArticle normalized;
                try {
                    normalized = normalize(fetchedArticle, source.baseUrl());
                } catch (IllegalArgumentException exception) {
                    skipped++;
                    continue;
                }

                if (normalized.title() == null || normalized.title().isBlank()) {
                    skipped++;
                    continue;
                }
                if (!executionUrls.add(normalized.originalUrl())) {
                    skipped++;
                    continue;
                }
                importableArticles.add(normalized);
            }

            importResult = importService.importArticles(importableArticles, isDryRun());
            skipped += importResult.skipped();
        } catch (RuntimeException exception) {
            failures.add(new Failure(
                    source,
                    source.endpoint(),
                    conciseReason(exception)
            ));
        }

        return new SourceResult(
                source,
                fetched,
                importResult.inserted(),
                importResult.updated(),
                skipped,
                failures.size(),
                Duration.between(startedAt, Instant.now()),
                failures
        );
    }

    private List<CrawledArticle> fetch(TechArticleCrawlSource source) {
        return switch (source.crawlMethod()) {
            case RSS -> rssFeedParser.parse(httpClient.getXml(source.endpoint()), source);
            case ATOM -> atomFeedParser.parse(httpClient.getXml(source.endpoint()), source);
            case HTML -> woowaHtmlParser.parse(httpClient.getHtml(source.endpoint()));
        };
    }

    private boolean isPublishedFromConfiguredDate(CrawledArticle article) {
        if (article.publishedAt() == null) {
            return false;
        }
        return !article.publishedAt()
                .atZone(PUBLISHED_AT_ZONE)
                .toLocalDate()
                .isBefore(properties.getPublishedFrom());
    }

    private CrawledArticle normalize(CrawledArticle article, String baseUrl) {
        String normalizedUrl = urlCanonicalizer.canonicalize(article.originalUrl(), baseUrl);

        return new CrawledArticle(
                article.source(),
                htmlTextExtractor.abbreviate(article.title(), TITLE_MAX_LENGTH),
                normalizedUrl,
                article.publishedAt()
        );
    }

    private void logSourceResult(SourceResult result) {
        String prefix = isDryRun() ? "[DRY-RUN] " : "";
        log.info("{}[{}] fetched={}, inserted={}, updated={}, skipped={}, failed={}, elapsedMs={}",
                prefix,
                result.source(),
                result.fetched(),
                result.inserted(),
                result.updated(),
                result.skipped(),
                result.failed(),
                result.duration().toMillis());
    }

    private String conciseReason(Throwable throwable) {
        String message = throwable.getMessage();
        String reason = message == null || message.isBlank()
                ? throwable.getClass().getSimpleName()
                : message;
        return htmlTextExtractor.abbreviate(reason.replaceAll("\\s+", " "), 300);
    }

    private boolean isDryRun() {
        return Boolean.TRUE.equals(properties.getDryRun());
    }
}
