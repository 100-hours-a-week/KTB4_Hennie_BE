package com.hennie.springdatajpa.crawler.runner;

import com.hennie.springdatajpa.crawler.service.TechArticleCrawler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "crawler", name = "enabled", havingValue = "true")
public class OneTimeCrawlerRunner implements ApplicationRunner {

    private final TechArticleCrawler techArticleCrawler;

    @Override
    public void run(ApplicationArguments args) {
        log.info("일회성 기술 아티클 크롤러를 시작합니다.");
        try {
            techArticleCrawler.crawl();
        } catch (RuntimeException exception) {
            log.error("일회성 기술 아티클 크롤러가 예상하지 못한 오류로 종료되었습니다. 서버 시작은 계속합니다. reason={}",
                    exception.getMessage());
        }
    }
}
