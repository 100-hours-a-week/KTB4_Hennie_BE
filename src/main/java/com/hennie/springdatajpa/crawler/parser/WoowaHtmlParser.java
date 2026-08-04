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

@Component
@RequiredArgsConstructor
public class WoowaHtmlParser { // 정적 HTML 방식

    private final HtmlTextExtractor htmlTextExtractor;
    private final PublicationDateParser publicationDateParser;

    public List<CrawledArticle> parse(Document document) {
        List<CrawledArticle> articles = new ArrayList<>();

        // 2026-08-03 실제 서버 렌더링 목록 HTML에서 확인한 selector이다.
        for (Element item : document.select(".post-list .post-item.firstpaint")) {
            Element link = item.selectFirst("a[href]");
            Element title = item.selectFirst(".post-title");
            if (link == null || title == null) {
                continue;
            }

            articles.add(new CrawledArticle(
                    TechArticleSource.WOOWA,
                    htmlTextExtractor.extract(title.text()),
                    link.absUrl("href"),
                    publicationDateParser.parse(text(item, ".post-author-date"))
            ));
        }
        return articles;
    }

    private String text(Element parent, String selector) {
        Element element = parent.selectFirst(selector);
        return element == null ? null : htmlTextExtractor.extract(element.text());
    }
}
