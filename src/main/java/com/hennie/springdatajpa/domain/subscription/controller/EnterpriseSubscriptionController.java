package com.hennie.springdatajpa.domain.subscription.controller;

import com.hennie.springdatajpa.domain.subscription.dto.response.EnterpriseSubscriptionListResponseDto;
import com.hennie.springdatajpa.domain.subscription.service.EnterpriseSubscriptionService;
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
@RequestMapping("/enterprise-subscriptions")
@RequiredArgsConstructor
public class EnterpriseSubscriptionController {

    private final EnterpriseSubscriptionService enterpriseSubscriptionService;

    @PutMapping("/{enterpriseId}")
    public ResponseEntity<Void> subscribe(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long enterpriseId
    ) {
        enterpriseSubscriptionService.subscribe(userId, enterpriseId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{enterpriseId}")
    public ResponseEntity<Void> unsubscribe(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long enterpriseId
    ) {
        enterpriseSubscriptionService.unsubscribe(userId, enterpriseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<EnterpriseSubscriptionListResponseDto>> getMySubscriptions(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.of(
                "ENTERPRISE_SUBSCRIPTIONS_FOUND",
                enterpriseSubscriptionService.getMySubscriptions(userId)
        ));
    }
}
