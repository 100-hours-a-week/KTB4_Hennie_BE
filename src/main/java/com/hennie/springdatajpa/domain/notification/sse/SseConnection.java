package com.hennie.springdatajpa.domain.notification.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public record SseConnection(
        Long userId,
        String connectionId,
        SseEmitter emitter
) {
}
