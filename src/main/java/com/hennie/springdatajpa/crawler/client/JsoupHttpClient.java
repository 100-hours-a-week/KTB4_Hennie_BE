package com.hennie.springdatajpa.crawler.client;

import com.hennie.springdatajpa.crawler.config.CrawlerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsoupHttpClient { // 수집 방식에 따라 엔드포인트 호출 - HTTP 요청 및 응답 처리

    private static final String ACCEPT_HEADER =
            "application/rss+xml, application/atom+xml, application/xml, text/html;q=0.9, */*;q=0.8";

    private final CrawlerProperties properties;
    private long lastRequestStartedAt;

    public Document getXml(String url) {
        Connection.Response response = execute(url);
        return Jsoup.parse(
                response.body(),
                response.url().toExternalForm(),
                Parser.xmlParser()
        );
    }

    public Document getHtml(String url) {
        Connection.Response response = execute(url);
        return Jsoup.parse(response.body(), response.url().toExternalForm());
    }

    private Connection.Response execute(String url) {
        RuntimeException lastFailure = null;

        for (int attempt = 1; attempt <= properties.getMaxAttempts(); attempt++) {
            waitForRequestDelay();

            try {
                Connection.Response response = Jsoup.connect(url)
                        .userAgent(properties.getUserAgent())
                        .header("Accept", ACCEPT_HEADER)
                        .timeout(properties.getTimeoutMs())
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true)
                        .followRedirects(true)
                        .maxBodySize(properties.getMaxResponseBytes())
                        .method(Connection.Method.GET)
                        .execute();

                int statusCode = response.statusCode();
                if (statusCode >= 200 && statusCode < 300) {
                    return response;
                }

                if (statusCode == 403) {
                    log.warn("크롤링 요청이 403으로 거부되어 우회 없이 건너뜁니다. url={}", url);
                    throw new CrawlerHttpException(url, statusCode, "HTTP 403 Forbidden");
                }

                CrawlerHttpException failure = new CrawlerHttpException(
                        url,
                        statusCode,
                        "HTTP " + statusCode
                );
                if (!isRetryable(statusCode) || attempt == properties.getMaxAttempts()) {
                    throw failure;
                }
                lastFailure = failure;
                log.warn("크롤링 요청 재시도 예정. url={}, status={}, attempt={}/{}",
                        url, statusCode, attempt, properties.getMaxAttempts());
            } catch (IOException exception) {
                lastFailure = new CrawlerHttpException(url, null, conciseMessage(exception), exception);
                if (attempt == properties.getMaxAttempts()) {
                    throw lastFailure;
                }
                log.warn("크롤링 네트워크 오류로 재시도 예정. url={}, attempt={}/{}, reason={}",
                        url, attempt, properties.getMaxAttempts(), conciseMessage(exception));
            }
        }

        throw lastFailure == null
                ? new CrawlerHttpException(url, null, "알 수 없는 HTTP 요청 실패")
                : lastFailure;
    }

    private synchronized void waitForRequestDelay() {
        long now = System.currentTimeMillis();
        long waitMs = properties.getRequestDelayMs() - (now - lastRequestStartedAt);

        if (waitMs > 0) {
            try {
                Thread.sleep(waitMs);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new CrawlerHttpException(null, null, "요청 대기 중 인터럽트됨", exception);
            }
        }
        lastRequestStartedAt = System.currentTimeMillis();
    }

    private boolean isRetryable(int statusCode) {
        return statusCode == 408 || statusCode == 429 || statusCode >= 500;
    }

    private String conciseMessage(Exception exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank()
                ? exception.getClass().getSimpleName()
                : message;
    }

    public static class CrawlerHttpException extends RuntimeException {
        private final String url;
        private final Integer statusCode;

        public CrawlerHttpException(String url, Integer statusCode, String message) {
            super(message);
            this.url = url;
            this.statusCode = statusCode;
        }

        public CrawlerHttpException(String url, Integer statusCode, String message, Throwable cause) {
            super(message, cause);
            this.url = url;
            this.statusCode = statusCode;
        }

        public String getUrl() {
            return url;
        }

        public Integer getStatusCode() {
            return statusCode;
        }
    }
}
