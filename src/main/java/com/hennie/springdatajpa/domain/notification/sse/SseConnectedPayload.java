package com.hennie.springdatajpa.domain.notification.sse;

import java.time.Instant;

public record SseConnectedPayload(
        int schemaVersion,
        String connectionId,
        Instant occurredAt
) {
    public static final int SCHEMA_VERSION = 1;
}
