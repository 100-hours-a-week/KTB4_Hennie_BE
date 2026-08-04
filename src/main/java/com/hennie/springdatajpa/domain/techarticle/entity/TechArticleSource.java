package com.hennie.springdatajpa.domain.techarticle.entity;

public enum TechArticleSource {

    KAKAO(
            CrawlMethod.RSS,
            "https://tech.kakao.com/feed",
            "https://tech.kakao.com/"
    ),
    NAVER_D2(
            CrawlMethod.ATOM,
            "https://d2.naver.com/d2.atom",
            "https://d2.naver.com/"
    ),
    TOSS(
            CrawlMethod.RSS,
            "https://toss.tech/rss.xml",
            "https://toss.tech/"
    ),
    COUPANG(
            CrawlMethod.RSS,
            "https://medium.com/feed/coupang-engineering",
            "https://medium.com/coupang-engineering/"
    ),
    WOOWA(
            CrawlMethod.HTML,
            "https://techblog.woowahan.com/",
            "https://techblog.woowahan.com/"
    ),
    DAANGN(
            CrawlMethod.RSS,
            "https://medium.com/feed/daangn",
            "https://medium.com/daangn/"
    ),
    OLIVE_YOUNG(
            CrawlMethod.RSS,
            "https://oliveyoung.tech/rss.xml",
            "https://oliveyoung.tech/"
    );

    private final CrawlMethod crawlMethod;
    private final String endpoint;
    private final String baseUrl;

    TechArticleSource(CrawlMethod crawlMethod, String endpoint, String baseUrl) {
        this.crawlMethod = crawlMethod;
        this.endpoint = endpoint;
        this.baseUrl = baseUrl;
    }

    public CrawlMethod crawlMethod() {
        return crawlMethod;
    }

    public String endpoint() {
        return endpoint;
    }

    public String baseUrl() {
        return baseUrl;
    }

    public enum CrawlMethod {
        RSS,
        ATOM,
        HTML
    }
}
