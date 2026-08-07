package com.hennie.springdatajpa.domain.subscription.service;

import com.hennie.springdatajpa.domain.enterprise.entity.Enterprise;
import com.hennie.springdatajpa.domain.enterprise.repository.EnterpriseRepository;
import com.hennie.springdatajpa.domain.subscription.dto.response.ArticleSubscriptionListResponseDto;
import com.hennie.springdatajpa.domain.subscription.entity.ArticleSubscription;
import com.hennie.springdatajpa.domain.subscription.repository.ArticleSubscriptionRepository;
import com.hennie.springdatajpa.domain.user.entity.User;
import com.hennie.springdatajpa.domain.user.repository.UserRepository;
import com.hennie.springdatajpa.global.exception.DuplicateResourceException;
import com.hennie.springdatajpa.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleSubscriptionService {

    private final ArticleSubscriptionRepository articleSubscriptionRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final UserRepository userRepository;

    @Transactional
    public void subscribe(Long userId, Long enterpriseId) {
        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        Enterprise enterprise = getEnterprise(enterpriseId);
        if (!enterprise.canSubscribe()) {
            throw new DuplicateResourceException("ENTERPRISE_INACTIVE");
        }
        ArticleSubscription subscription = articleSubscriptionRepository
                .findByUserIdAndEnterpriseId(userId, enterpriseId)
                .orElseGet(() -> new ArticleSubscription(user, enterprise));

        if (subscription.getId() == null) {
            articleSubscriptionRepository.save(subscription);
            return;
        }
        if (!subscription.isStatus()) {
            subscription.activate();
        }
    }

    @Transactional
    public void unsubscribe(Long userId, Long enterpriseId) {
        userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        getEnterprise(enterpriseId);

        articleSubscriptionRepository
                .findByUserIdAndEnterpriseId(userId, enterpriseId)
                .ifPresent(articleSubscriptionRepository::delete);
    }

    @Transactional(readOnly = true)
    public ArticleSubscriptionListResponseDto getMySubscriptions(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("USER_NOT_FOUND");
        }
        return new ArticleSubscriptionListResponseDto(
                articleSubscriptionRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
        );
    }

    private Enterprise getEnterprise(Long enterpriseId) {
        return enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new NotFoundException("ENTERPRISE_NOT_FOUND"));
    }
}
