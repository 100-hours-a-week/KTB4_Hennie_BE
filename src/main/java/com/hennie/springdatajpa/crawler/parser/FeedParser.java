package com.hennie.springdatajpa.crawler.parser;

import com.hennie.springdatajpa.crawler.model.CrawledArticle;
import com.hennie.springdatajpa.domain.enterprise.entity.TechArticleCrawlSource;
import org.jsoup.nodes.Document;

import java.util.List;

public interface FeedParser {
    List<CrawledArticle> parse(
            Document document,
            TechArticleCrawlSource source
    );
}
