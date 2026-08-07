package com.hennie.springdatajpa.domain.notification.event;

import com.hennie.springdatajpa.domain.notification.service.NotificationSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
public class NotificationSseEventListener {

    private final NotificationSseService notificationSseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NotificationCreatedEvent event) {
        notificationSseService.send(
                event.recipientId(),
                SseEmitter.event()
                        .id(event.eventId())
                        .name(event.eventType())
                        .data(event)
        );
    }
}
