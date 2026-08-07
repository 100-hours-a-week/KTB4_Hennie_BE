package com.hennie.springdatajpa.domain.subscription.entity;

import com.hennie.springdatajpa.domain.enterprise.entity.Enterprise;
import com.hennie.springdatajpa.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "article_subscription",
        indexes = {
                @Index(
                        name = "ix_article_subscription_enterprise_status",
                        columnList = "enterprise_id, status"
                ),
                @Index(
                        name = "ix_article_subscription_user_created",
                        columnList = "user_id, created_at"
                )
        },
        uniqueConstraints = @UniqueConstraint(
                name = "uq_article_subscription_user_enterprise",
                columnNames = {"user_id", "enterprise_id"}
        )
)
@Getter
public class ArticleSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_subscription_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enterprise_id", nullable = false)
    private Enterprise enterprise;

    @Column(nullable = false)
    private boolean status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected ArticleSubscription() {
    }

    public ArticleSubscription(User user, Enterprise enterprise) {
        this.user = user;
        this.enterprise = enterprise;
        activate();
    }

    public void activate() {
        this.status = true;
        this.createdAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = false;
    }
}
