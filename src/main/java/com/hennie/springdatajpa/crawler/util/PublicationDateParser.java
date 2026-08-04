package com.hennie.springdatajpa.crawler.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

@Component
public class PublicationDateParser {

    private static final DateTimeFormatter WOOWA_DATE =
            DateTimeFormatter.ofPattern("MMM.dd.uuuu", Locale.ENGLISH);

    public Instant parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String candidate = value.trim();
        List<DateParser> parsers = List.of(
                text -> Instant.parse(text),
                text -> OffsetDateTime.parse(text).toInstant(),
                text -> ZonedDateTime.parse(text, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant(),
                text -> LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        .toInstant(ZoneOffset.UTC),
                text -> LocalDate.parse(text, WOOWA_DATE).atStartOfDay(ZoneOffset.UTC).toInstant()
        );

        for (DateParser parser : parsers) {
            try {
                return parser.parse(candidate);
            } catch (DateTimeParseException ignored) {
                // 다음으로 지원하는 형식을 시도한다.
            }
        }
        return null;
    }

    @FunctionalInterface
    private interface DateParser {
        Instant parse(String value);
    }
}
