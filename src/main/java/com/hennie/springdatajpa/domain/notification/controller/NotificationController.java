package com.hennie.springdatajpa.domain.notification.controller;

import com.hennie.springdatajpa.domain.notification.dto.response.NotificationListResponseDto;
import com.hennie.springdatajpa.domain.notification.dto.response.UnreadNotificationCountResponseDto;
import com.hennie.springdatajpa.domain.notification.service.NotificationQueryService;
import com.hennie.springdatajpa.domain.notification.service.NotificationReadService;
import com.hennie.springdatajpa.domain.notification.service.NotificationSseService;
import com.hennie.springdatajpa.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationQueryService notificationQueryService;
    private final NotificationReadService notificationReadService;
    private final NotificationSseService notificationSseService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@AuthenticationPrincipal Long userId) {
        return notificationSseService.connect(userId);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<NotificationListResponseDto>> getNotifications(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.of(
                "NOTIFICATIONS_FOUND",
                notificationQueryService.getNotifications(userId, page, size)
        ));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadNotificationCountResponseDto>> getUnreadCount(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.of(
                "UNREAD_NOTIFICATION_COUNT_FOUND",
                notificationQueryService.getUnreadCount(userId)
        ));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long notificationId
    ) {
        notificationReadService.markAsRead(userId, notificationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal Long userId
    ) {
        notificationReadService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }
}
