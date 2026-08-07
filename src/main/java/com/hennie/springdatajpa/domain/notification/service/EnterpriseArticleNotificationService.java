package com.hennie.springdatajpa.domain.notification.service;

import com.hennie.springdatajpa.domain.notification.entity.Notification;
import com.hennie.springdatajpa.domain.notification.entity.NotificationType;
import com.hennie.springdatajpa.domain.notification.event.NotificationCreatedEvent;
import com.hennie.springdatajpa.domain.notification.repository.NotificationRepository;
import com.hennie.springdatajpa.domain.subscription.entity.EnterpriseSubscription;
import com.hennie.springdatajpa.domain.subscription.repository.EnterpriseSubscriptionRepository;
import com.hennie.springdatajpa.domain.techarticle.entity.TechArticle;
import com.hennie.springdatajpa.domain.techarticle.event.TechArticleCreatedEvent;
import com.hennie.springdatajpa.domain.techarticle.repository.TechArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EnterpriseArticleNotificationService {

    private static final String MESSAGE =
            "%s에 새로운 기술 게시글이 등록되었습니다.";

    private final TechArticleRepository techArticleRepository;
    private final EnterpriseSubscriptionRepository enterpriseSubscriptionRepository;
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createForNewArticle(TechArticleCreatedEvent event) {
        TechArticle article = techArticleRepository.findByIdWithEnterprise(event.articleId())
                .orElseThrow(() -> new IllegalStateException(
                        "TECH_ARTICLE_NOT_FOUND: " + event.articleId()
                ));
        if (!Objects.equals(article.getEnterprise().getId(), event.enterpriseId())) {
            throw new IllegalStateException(
                    "TECH_ARTICLE_ENTERPRISE_MISMATCH: " + event.articleId()
            );
        }

        List<EnterpriseSubscription> subscriptions = enterpriseSubscriptionRepository
                .findActiveRecipientsForNotification(event.enterpriseId());
        if (subscriptions.isEmpty()) {
            return;
        }

        Set<Long> notifiedRecipientIds = notificationRepository
                .findRecipientIdsByNotificationTypeAndArticleId(
                        NotificationType.SUBSCRIBED_ENTERPRISE_ARTICLE,
                        article.getId()
        );
        List<Notification> notifications = subscriptions.stream()
                .filter(EnterpriseSubscription::isStatus)
                .filter(subscription -> !subscription.getUser().isAuthorDeleted())
                .filter(subscription -> !notifiedRecipientIds.contains(
                        subscription.getUser().getId()
                ))
                .map(subscription -> Notification.enterpriseArticle(
                        subscription.getUser(),
                        article,
                        article.getEnterprise(),
                        MESSAGE.formatted(article.getEnterprise().getName())
                ))
                .toList();
        if (notifications.isEmpty()) {
            return;
        }

        notificationRepository.saveAllAndFlush(notifications).stream()
                .map(NotificationCreatedEvent::from)
                .forEach(eventPublisher::publishEvent);
    }
}
