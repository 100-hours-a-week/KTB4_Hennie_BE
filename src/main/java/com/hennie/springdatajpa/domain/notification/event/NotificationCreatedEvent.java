package com.hennie.springdatajpa.domain.notification.event;

import com.hennie.springdatajpa.domain.notification.entity.Notification;
import com.hennie.springdatajpa.domain.notification.entity.NotificationType;

import java.time.Instant;
import java.util.Objects;

public record NotificationCreatedEvent(
        String eventId,
        String eventType,
        int schemaVersion,
        Instant occurredAt,
        Long notificationId,
        Long recipientId,
        NotificationType notificationType,
        Long enterpriseId
) {
    public static final String EVENT_TYPE = "notification.created";
    public static final int SCHEMA_VERSION = 1;

    public static NotificationCreatedEvent from(Notification notification) {
        Long notificationId = Objects.requireNonNull(
                notification.getId(),
                "Saved notification ID must not be null"
        );

        return new NotificationCreatedEvent(
                "notification-" + notificationId,
                EVENT_TYPE,
                SCHEMA_VERSION,
                Instant.now(),
                notificationId,
                notification.getRecipient().getId(),
                notification.getNotificationType(),
                notification.getEnterprise() == null
                        ? null
                        : notification.getEnterprise().getId()
        );
    }
}
