package com.hennie.springdatajpa.domain.notification.dto.response;

import com.hennie.springdatajpa.domain.notification.entity.Notification;
import com.hennie.springdatajpa.domain.notification.entity.NotificationType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponseDto {

    private final Long notificationId;
    private final NotificationType notificationType;
    private final String message;
    private final Long actorId;
    private final Long postId;
    private final Long commentId;
    private final Long articleId;
    private final Long enterpriseId;
    private final LocalDateTime readAt;
    private final LocalDateTime createdAt;

    public NotificationResponseDto(Notification notification) {
        this.notificationId = notification.getId();
        this.notificationType = notification.getNotificationType();
        this.message = notification.getMessage();
        this.actorId = notification.getActor() == null
                ? null
                : notification.getActor().getId();
        this.postId = notification.getPost() == null
                ? null
                : notification.getPost().getId();
        this.commentId = notification.getComment() == null
                ? null
                : notification.getComment().getId();
        this.articleId = notification.getArticle() == null
                ? null
                : notification.getArticle().getId();
        this.enterpriseId = notification.getEnterprise() == null
                ? null
                : notification.getEnterprise().getId();
        this.readAt = notification.getReadAt();
        this.createdAt = notification.getCreatedAt();
    }
}
