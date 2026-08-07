package com.hennie.springdatajpa.domain.subscription.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ArticleSubscriptionResponseDto {

    private final Long enterpriseId;
    private final boolean status;
}
