package com.hennie.springdatajpa.domain.notification.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class SseEmitterRegistry {

    private final ConcurrentMap<Long, ConcurrentMap<String, SseEmitter>> emittersByUserId =
            new ConcurrentHashMap<>();

    public SseConnection register(Long userId, SseEmitter emitter) {
        String connectionId = UUID.randomUUID().toString();
        SseConnection connection = new SseConnection(userId, connectionId, emitter);

        emittersByUserId.compute(userId, (id, currentEmitters) -> {
            ConcurrentMap<String, SseEmitter> emitters = currentEmitters == null
                    ? new ConcurrentHashMap<>()
                    : currentEmitters;
            emitters.put(connectionId, emitter);
            return emitters;
        });

        emitter.onCompletion(() -> remove(connection));
        emitter.onTimeout(() -> remove(connection));
        emitter.onError(exception -> remove(connection));

        return connection;
    }

    public List<SseConnection> findAllByUserId(Long userId) {
        ConcurrentMap<String, SseEmitter> emitters = emittersByUserId.get(userId);
        return emitters == null
                ? List.of()
                : emitters.entrySet().stream()
                        .map(entry -> new SseConnection(
                                userId,
                                entry.getKey(),
                                entry.getValue()
                        ))
                        .toList();
    }

    public boolean remove(SseConnection connection) {
        AtomicBoolean removed = new AtomicBoolean(false);

        emittersByUserId.computeIfPresent(connection.userId(), (userId, emitters) -> {
            removed.set(emitters.remove(
                    connection.connectionId(),
                    connection.emitter()
            ));
            return emitters.isEmpty() ? null : emitters;
        });

        return removed.get();
    }
}
