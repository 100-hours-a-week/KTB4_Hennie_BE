package com.hennie.springdatajpa.domain.notification.service;

import com.hennie.springdatajpa.domain.notification.sse.SseConnection;
import com.hennie.springdatajpa.domain.notification.sse.SseEmitterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class NotificationSseService {

    static final Duration CONNECTION_TIMEOUT = Duration.ofHours(1);

    private final SseEmitterRegistry sseEmitterRegistry;

    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(CONNECTION_TIMEOUT.toMillis());
        sseEmitterRegistry.register(userId, emitter);
        return emitter;
    }

    public void send(Long userId, SseEmitter.SseEventBuilder event) {
        for (SseConnection connection : sseEmitterRegistry.findAllByUserId(userId)) {
            try {
                connection.emitter().send(event);
            } catch (IOException | IllegalStateException exception) {
                sseEmitterRegistry.remove(connection);
            }
        }
    }
}
