package com.hennie.springdatajpa.domain.notification.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class NotificationListResponseDto {

    private final List<NotificationResponseDto> notifications;
    private final int page;
    private final int size;
    private final long totalCount;
    private final int totalPages;
    private final boolean hasNext;
}
