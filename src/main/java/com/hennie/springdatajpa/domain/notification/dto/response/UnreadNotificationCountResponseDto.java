package com.hennie.springdatajpa.domain.notification.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UnreadNotificationCountResponseDto {

    private final long unreadCount;
}
