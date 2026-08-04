package com.hennie.springdatajpa.crawler.parser;

import com.hennie.springdatajpa.crawler.model.CrawledArticle;
import com.hennie.springdatajpa.crawler.util.HtmlTextExtractor;
import com.hennie.springdatajpa.crawler.util.PublicationDateParser;
import com.hennie.springdatajpa.domain.techarticle.entity.TechArticleSource;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.hennie.springdatajpa.crawler.util.CrawlerTextUtils.firstNonBlank;

@Component
@RequiredArgsConstructor
public class AtomFeedParser implements FeedParser { // Atom 방식

    private final HtmlTextExtractor htmlTextExtractor;
    private final PublicationDateParser publicationDateParser;

    @Override
    public List<CrawledArticle> parse(
            Document document,
            TechArticleSource source
    ) {
        List<CrawledArticle> articles = new ArrayList<>();

        for (Element entry : document.select("entry")) {
            Element link = entry.children().stream()
                    .filter(element -> element.tagName().equalsIgnoreCase("link"))
                    .filter(element -> element.attr("rel").isBlank()
                            || element.attr("rel").equalsIgnoreCase("alternate"))
                    .findFirst()
                    .orElse(null);
            String originalUrl = link == null ? childText(entry, "id") : link.absUrl("href");
            if (originalUrl == null || originalUrl.isBlank()) {
                originalUrl = link == null ? null : link.attr("href");
            }

            articles.add(new CrawledArticle(
                    source,
                    htmlTextExtractor.extract(childText(entry, "title")),
                    originalUrl,
                    publicationDateParser.parse(firstNonBlank(
                            childText(entry, "published"),
                            childText(entry, "updated")
                    ))
            ));
        }
        return articles;
    }

    private String childText(Element parent, String tagName) {
        return parent.children().stream()
                .filter(element -> element.tagName().equalsIgnoreCase(tagName))
                .findFirst()
                .map(Element::text)
                .orElse(null);
    }

}
