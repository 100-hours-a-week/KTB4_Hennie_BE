package com.hennie.springdatajpa.domain.subscription.controller;

import com.hennie.springdatajpa.domain.subscription.dto.response.ArticleSubscriptionListResponseDto;
import com.hennie.springdatajpa.domain.subscription.service.ArticleSubscriptionService;
import com.hennie.springdatajpa.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article-subscriptions")
@RequiredArgsConstructor
public class ArticleSubscriptionController {

    private final ArticleSubscriptionService articleSubscriptionService;

    @PutMapping("/{enterpriseId}")
    public ResponseEntity<Void> subscribe(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long enterpriseId
    ) {
        articleSubscriptionService.subscribe(userId, enterpriseId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{enterpriseId}")
    public ResponseEntity<Void> unsubscribe(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long enterpriseId
    ) {
        articleSubscriptionService.unsubscribe(userId, enterpriseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ArticleSubscriptionListResponseDto>> getMySubscriptions(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.of(
                "ARTICLE_SUBSCRIPTIONS_FOUND",
                articleSubscriptionService.getMySubscriptions(userId)
        ));
    }

}
