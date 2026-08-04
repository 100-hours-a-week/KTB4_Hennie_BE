package com.hennie.springdatajpa.crawler.util;

import java.util.Arrays;

public final class CrawlerTextUtils {

    private CrawlerTextUtils() {
    }

    public static String firstNonBlank(String... values) {
        return Arrays.stream(values)
                .filter(value -> value != null && !value.isBlank())
                .findFirst()
                .orElse(null);
    }
}
