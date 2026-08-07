package com.hennie.springdatajpa.crawler.model;

import com.hennie.springdatajpa.domain.enterprise.entity.TechArticleCrawlSource;

import java.time.Duration;
import java.util.List;

public record CrawlReport(List<SourceResult> sourceResults) {
    public CrawlReport {
        sourceResults = List.copyOf(sourceResults);
    }

    public int totalFetched() {
        return sourceResults.stream().mapToInt(SourceResult::fetched).sum();
    }

    public int totalInserted() {
        return sourceResults.stream().mapToInt(SourceResult::inserted).sum();
    }

    public int totalUpdated() {
        return sourceResults.stream().mapToInt(SourceResult::updated).sum();
    }

    public int totalSkipped() {
        return sourceResults.stream().mapToInt(SourceResult::skipped).sum();
    }

    public int totalFailed() {
        return sourceResults.stream().mapToInt(SourceResult::failed).sum();
    }

    public List<Failure> failures() {
        return sourceResults.stream()
                .flatMap(result -> result.failures().stream())
                .toList();
    }

    public record SourceResult(
            TechArticleCrawlSource source,
            int fetched,
            int inserted,
            int updated,
            int skipped,
            int failed,
            Duration duration,
            List<Failure> failures
    ) {
        public SourceResult {
            failures = List.copyOf(failures);
        }
    }

    public record Failure(
            TechArticleCrawlSource source,
            String url,
            String reason
    ) {
    }
}
