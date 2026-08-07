package com.hennie.springdatajpa.domain.subscription.service;

import com.hennie.springdatajpa.domain.enterprise.entity.Enterprise;
import com.hennie.springdatajpa.domain.enterprise.repository.EnterpriseRepository;
import com.hennie.springdatajpa.domain.subscription.dto.response.EnterpriseSubscriptionListResponseDto;
import com.hennie.springdatajpa.domain.subscription.entity.EnterpriseSubscription;
import com.hennie.springdatajpa.domain.subscription.repository.EnterpriseSubscriptionRepository;
import com.hennie.springdatajpa.domain.user.entity.User;
import com.hennie.springdatajpa.domain.user.repository.UserRepository;
import com.hennie.springdatajpa.global.exception.DuplicateResourceException;
import com.hennie.springdatajpa.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnterpriseSubscriptionService {

    private final EnterpriseSubscriptionRepository enterpriseSubscriptionRepository;
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
        EnterpriseSubscription subscription = enterpriseSubscriptionRepository
                .findByUserIdAndEnterpriseId(userId, enterpriseId)
                .orElseGet(() -> new EnterpriseSubscription(user, enterprise));

        if (subscription.getId() == null) {
            enterpriseSubscriptionRepository.save(subscription);
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

        enterpriseSubscriptionRepository
                .findByUserIdAndEnterpriseId(userId, enterpriseId)
                .ifPresent(EnterpriseSubscription::deactivate);
    }

    @Transactional(readOnly = true)
    public EnterpriseSubscriptionListResponseDto getMySubscriptions(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("USER_NOT_FOUND");
        }
        return new EnterpriseSubscriptionListResponseDto(
                enterpriseSubscriptionRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
        );
    }

    private Enterprise getEnterprise(Long enterpriseId) {
        return enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new NotFoundException("ENTERPRISE_NOT_FOUND"));
    }
}
