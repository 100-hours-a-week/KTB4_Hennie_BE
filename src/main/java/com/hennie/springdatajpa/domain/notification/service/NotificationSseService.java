package com.hennie.springdatajpa.domain.notification.service;

import com.hennie.springdatajpa.domain.notification.sse.SseConnection;
import com.hennie.springdatajpa.domain.notification.sse.SseConnectedPayload;
import com.hennie.springdatajpa.domain.notification.sse.SseEmitterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class NotificationSseService {

    static final String CONNECTED_EVENT_NAME = "connected";
    static final String HEARTBEAT_COMMENT = "heartbeat";
    static final Duration CONNECTION_TIMEOUT = Duration.ofHours(1);

    private final SseEmitterRegistry sseEmitterRegistry;

    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(CONNECTION_TIMEOUT.toMillis());
        SseConnection connection = sseEmitterRegistry.register(userId, emitter);
        send(connection, connectedEvent(connection));
        return emitter;
    }

    public void send(Long userId, SseEmitter.SseEventBuilder event) {
        for (SseConnection connection : sseEmitterRegistry.findAllByUserId(userId)) {
            send(connection, event);
        }
    }

    public void sendHeartbeat() {
        SseEmitter.SseEventBuilder heartbeat = SseEmitter.event()
                .comment(HEARTBEAT_COMMENT);

        for (SseConnection connection : sseEmitterRegistry.findAll()) {
            send(connection, heartbeat);
        }
    }

    private SseEmitter.SseEventBuilder connectedEvent(SseConnection connection) {
        return SseEmitter.event()
                .id(connection.connectionId())
                .name(CONNECTED_EVENT_NAME)
                .data(new SseConnectedPayload(
                        SseConnectedPayload.SCHEMA_VERSION,
                        connection.connectionId(),
                        Instant.now()
                ));
    }

    private void send(SseConnection connection, SseEmitter.SseEventBuilder event) {
        try {
            connection.emitter().send(event);
        } catch (IOException | IllegalStateException exception) {
            sseEmitterRegistry.remove(connection);
        }
    }
}
