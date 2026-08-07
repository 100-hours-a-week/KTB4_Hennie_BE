package com.hennie.springdatajpa.domain.notification.entity;

import com.hennie.springdatajpa.domain.comment.entity.Comment;
import com.hennie.springdatajpa.domain.enterprise.entity.Enterprise;
import com.hennie.springdatajpa.domain.post.entity.Post;
import com.hennie.springdatajpa.domain.techarticle.entity.TechArticle;
import com.hennie.springdatajpa.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notification",
        indexes = {
                @Index(
                        name = "ix_notification_recipient_created",
                        columnList = "recipient_id, created_at"
                ),
                @Index(
                        name = "ix_notification_recipient_read_created",
                        columnList = "recipient_id, read_at, created_at"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_notification_recipient_type_comment",
                        columnNames = {"recipient_id", "notification_type", "comment_id"}
                ),
                @UniqueConstraint(
                        name = "uq_notification_recipient_type_article",
                        columnNames = {"recipient_id", "notification_type", "article_id"}
                )
        }
)
@Getter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User actor;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private TechArticle article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enterprise_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Enterprise enterprise;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    private LocalDateTime readAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Notification() {
    }

    private Notification(
            User recipient,
            User actor,
            NotificationType notificationType,
            Post post,
            Comment comment,
            TechArticle article,
            Enterprise enterprise,
            String message
    ) {
        this.recipient = recipient;
        this.actor = actor;
        this.notificationType = notificationType;
        this.post = post;
        this.comment = comment;
        this.article = article;
        this.enterprise = enterprise;
        this.message = message;
    }

    public static Notification comment(
            User recipient,
            User actor,
            NotificationType notificationType,
            Post post,
            Comment comment,
            String message
    ) {
        return new Notification(
                recipient,
                actor,
                notificationType,
                post,
                comment,
                null,
                null,
                message
        );
    }

    public void markAsRead() {
        if (readAt == null) {
            readAt = LocalDateTime.now();
        }
    }
}
