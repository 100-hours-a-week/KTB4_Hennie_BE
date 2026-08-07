package com.hennie.springdatajpa.crawler.parser;

import com.hennie.springdatajpa.crawler.model.CrawledArticle;
import com.hennie.springdatajpa.crawler.util.HtmlTextExtractor;
import com.hennie.springdatajpa.crawler.util.PublicationDateParser;
import com.hennie.springdatajpa.domain.enterprise.entity.TechArticleCrawlSource;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.hennie.springdatajpa.crawler.util.CrawlerTextUtils.firstNonBlank;

@Component
@RequiredArgsConstructor
public class RssFeedParser implements FeedParser { // RSS 방식

    private final HtmlTextExtractor htmlTextExtractor;
    private final PublicationDateParser publicationDateParser;

    @Override
    public List<CrawledArticle> parse(
            Document document,
            TechArticleCrawlSource source
    ) {
        List<CrawledArticle> articles = new ArrayList<>();

        for (Element item : document.select("item")) {
            String title = htmlTextExtractor.extract(childContent(item, "title"));
            String originalUrl = firstNonBlank(
                    childContent(item, "link"),
                    childContent(item, "guid")
            );
            String published = firstNonBlank(
                    childContent(item, "pubdate"),
                    childContent(item, "published"),
                    childContent(item, "updated", "atom:updated")
            );

            articles.add(new CrawledArticle(
                    source,
                    title,
                    originalUrl,
                    publicationDateParser.parse(published)
            ));
        }
        return articles;
    }

    private String childContent(Element parent, String... names) {
        Element child = child(parent, names);
        return child == null ? null : child.text();
    }

    private Element child(Element parent, String... names) {
        List<String> expected = Arrays.stream(names)
                .map(value -> value.toLowerCase(Locale.ROOT))
                .toList();

        return parent.children().stream()
                .filter(element -> matches(element.tagName(), expected))
                .findFirst()
                .orElse(null);
    }

    private boolean matches(String tagName, List<String> expected) {
        String normalized = tagName.toLowerCase(Locale.ROOT);
        String localName = normalized.contains(":")
                ? normalized.substring(normalized.indexOf(':') + 1)
                : normalized;
        return expected.contains(normalized) || expected.contains(localName);
    }

}
