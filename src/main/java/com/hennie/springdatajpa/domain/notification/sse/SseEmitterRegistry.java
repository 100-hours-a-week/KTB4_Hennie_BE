package com.hennie.springdatajpa.domain.notification.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class SseEmitterRegistry {

    private final ConcurrentMap<Long, ConcurrentMap<String, SseEmitter>> emittersByUserId =
            new ConcurrentHashMap<>();

    public String register(Long userId, SseEmitter emitter) {
        String connectionId = UUID.randomUUID().toString();

        emittersByUserId.compute(userId, (id, currentEmitters) -> {
            ConcurrentMap<String, SseEmitter> emitters = currentEmitters == null
                    ? new ConcurrentHashMap<>()
                    : currentEmitters;
            emitters.put(connectionId, emitter);
            return emitters;
        });

        return connectionId;
    }

    public List<SseEmitter> findAllByUserId(Long userId) {
        ConcurrentMap<String, SseEmitter> emitters = emittersByUserId.get(userId);
        return emitters == null
                ? List.of()
                : List.copyOf(emitters.values());
    }
}
