package com.hennie.springdatajpa.domain.notification.sse;

import com.hennie.springdatajpa.domain.notification.event.NotificationCreatedEvent;
import com.hennie.springdatajpa.domain.notification.service.NotificationSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@RequiredArgsConstructor
public class EnterpriseNotificationSseBatcher {

    static final long FLUSH_INTERVAL_MILLIS = 1_000L;

    private final ConcurrentMap<BatchKey, NotificationCreatedEvent> pendingEvents =
            new ConcurrentHashMap<>();
    private final NotificationSseService notificationSseService;

    public void enqueue(NotificationCreatedEvent event) {
        Long enterpriseId = Objects.requireNonNull(
                event.enterpriseId(),
                "Enterprise notification must contain enterpriseId"
        );
        pendingEvents.put(new BatchKey(event.recipientId(), enterpriseId), event);
    }

    @Scheduled(
            fixedRate = FLUSH_INTERVAL_MILLIS,
            initialDelay = FLUSH_INTERVAL_MILLIS
    )
    public void flush() {
        pendingEvents.forEach((key, event) -> {
            if (pendingEvents.remove(key, event)) {
                notificationSseService.send(
                        event.recipientId(),
                        SseEmitter.event()
                                .id(event.eventId())
                                .name(event.eventType())
                                .data(event)
                );
            }
        });
    }

    private record BatchKey(Long recipientId, Long enterpriseId) {
    }
}
