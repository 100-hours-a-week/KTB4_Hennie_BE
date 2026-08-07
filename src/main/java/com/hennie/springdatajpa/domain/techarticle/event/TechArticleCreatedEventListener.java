package com.hennie.springdatajpa.domain.techarticle.event;

import com.hennie.springdatajpa.domain.notification.service.EnterpriseArticleNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TechArticleCreatedEventListener {

    private final EnterpriseArticleNotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TechArticleCreatedEvent event) {
        notificationService.createForNewArticle(event);
    }
}
