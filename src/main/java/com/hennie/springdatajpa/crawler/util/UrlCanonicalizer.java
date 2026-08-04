package com.hennie.springdatajpa.crawler.util;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UrlCanonicalizer {

    private static final Set<String> TRACKING_PARAMETERS = Set.of(
            "source", "sk", "fbclid", "gclid", "ref", "referrer"
    );

    public String canonicalize(String rawUrl, String baseUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new IllegalArgumentException("URL이 비어 있습니다.");
        }

        String resolved = baseUrl == null || baseUrl.isBlank()
                ? rawUrl.trim()
                : URI.create(baseUrl).resolve(rawUrl.trim()).toString();
        URI uri = URI.create(resolved).normalize();
        String scheme = lower(uri.getScheme());
        String host = lower(uri.getHost());

        if (!("http".equals(scheme) || "https".equals(scheme)) || host == null) {
            throw new IllegalArgumentException("HTTP(S) URL만 지원합니다: " + rawUrl);
        }

        int port = uri.getPort();
        boolean defaultPort = ("http".equals(scheme) && port == 80)
                || ("https".equals(scheme) && port == 443);
        String authority = defaultPort || port < 0 ? host : host + ":" + port;
        String path = normalizePath(uri.getRawPath());
        String query = normalizeQuery(uri.getRawQuery());

        return scheme + "://" + authority + path + (query == null ? "" : "?" + query);
    }

    private String normalizePath(String rawPath) {
        if (rawPath == null || rawPath.isBlank()) {
            return "/";
        }
        if (rawPath.length() > 1 && rawPath.endsWith("/")) {
            return rawPath.substring(0, rawPath.length() - 1);
        }
        return rawPath;
    }

    private String normalizeQuery(String rawQuery) {
        if (rawQuery == null || rawQuery.isBlank()) {
            return null;
        }

        String normalized = Arrays.stream(rawQuery.split("&"))
                .filter(parameter -> !parameter.isBlank())
                .filter(parameter -> !isTrackingParameter(parameter))
                .sorted()
                .collect(Collectors.joining("&"));
        return normalized.isBlank() ? null : normalized;
    }

    private boolean isTrackingParameter(String parameter) {
        String rawName = parameter.split("=", 2)[0];
        String name = URLDecoder.decode(rawName, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
        return name.startsWith("utm_") || TRACKING_PARAMETERS.contains(name);
    }

    private String lower(String value) {
        return value == null ? null : value.toLowerCase(Locale.ROOT);
    }
}
