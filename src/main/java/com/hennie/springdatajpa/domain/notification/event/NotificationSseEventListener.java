package com.hennie.springdatajpa.domain.notification.event;

import com.hennie.springdatajpa.domain.notification.entity.NotificationType;
import com.hennie.springdatajpa.domain.notification.service.NotificationSseService;
import com.hennie.springdatajpa.domain.notification.sse.EnterpriseNotificationSseBatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
public class NotificationSseEventListener {

    private final NotificationSseService notificationSseService;
    private final EnterpriseNotificationSseBatcher enterpriseNotificationSseBatcher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NotificationCreatedEvent event) {
        if (event.notificationType() == NotificationType.SUBSCRIBED_ENTERPRISE_ARTICLE) {
            enterpriseNotificationSseBatcher.enqueue(event);
            return;
        }

        notificationSseService.send(
                event.recipientId(),
                SseEmitter.event()
                        .id(event.eventId())
                        .name(event.eventType())
                        .data(event)
        );
    }
}
