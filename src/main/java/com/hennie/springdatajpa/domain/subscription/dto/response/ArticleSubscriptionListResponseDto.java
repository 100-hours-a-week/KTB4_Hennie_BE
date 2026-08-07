package com.hennie.springdatajpa.domain.subscription.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ArticleSubscriptionListResponseDto {

    private final List<ArticleSubscriptionResponseDto> subscriptions;
}
