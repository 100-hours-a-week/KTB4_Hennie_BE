package com.hennie.springdatajpa.crawler.util;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

@Component
public class HtmlTextExtractor {

    public String extract(String html) {
        if (html == null || html.isBlank()) {
            return null;
        }

        String text = Jsoup.parseBodyFragment(html).text()
                .replaceAll("\\s+", " ")
                .trim();
        if (text.isEmpty()) {
            return null;
        }
        return text;
    }

    public String abbreviate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        if (maxLength <= 0) {
            return "";
        }
        if (maxLength == 1) {
            return "…";
        }
        return text.substring(0, maxLength - 1).stripTrailing() + "…";
    }

}
