package com.hennie.springdatajpa.domain.notification.service;

import com.hennie.springdatajpa.domain.notification.dto.response.NotificationListResponseDto;
import com.hennie.springdatajpa.domain.notification.dto.response.NotificationResponseDto;
import com.hennie.springdatajpa.domain.notification.dto.response.UnreadNotificationCountResponseDto;
import com.hennie.springdatajpa.domain.notification.entity.Notification;
import com.hennie.springdatajpa.domain.notification.repository.NotificationRepository;
import com.hennie.springdatajpa.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public NotificationListResponseDto getNotifications(
            Long recipientId,
            int page,
            int size
    ) {
        validatePageParameters(page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Notification> notificationPage = notificationRepository
                .findAllByRecipientIdOrderByCreatedAtDesc(recipientId, pageable);
        List<NotificationResponseDto> notifications = notificationPage.getContent().stream()
                .map(NotificationResponseDto::new)
                .toList();

        return new NotificationListResponseDto(
                notifications,
                page,
                size,
                notificationPage.getTotalElements(),
                notificationPage.getTotalPages(),
                notificationPage.hasNext()
        );
    }

    @Transactional(readOnly = true)
    public UnreadNotificationCountResponseDto getUnreadCount(Long recipientId) {
        return new UnreadNotificationCountResponseDto(
                notificationRepository.countByRecipientIdAndReadAtIsNull(recipientId)
        );
    }

    private void validatePageParameters(int page, int size) {
        if (page < 1 || size < 1) {
            throw new BadRequestException("invalidPageParameter");
        }
    }
}
