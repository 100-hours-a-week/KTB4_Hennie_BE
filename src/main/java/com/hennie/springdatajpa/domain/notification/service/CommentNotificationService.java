package com.hennie.springdatajpa.domain.notification.service;

import com.hennie.springdatajpa.domain.comment.entity.Comment;
import com.hennie.springdatajpa.domain.notification.entity.Notification;
import com.hennie.springdatajpa.domain.notification.entity.NotificationType;
import com.hennie.springdatajpa.domain.notification.event.NotificationCreatedEvent;
import com.hennie.springdatajpa.domain.notification.repository.NotificationRepository;
import com.hennie.springdatajpa.domain.post.entity.Post;
import com.hennie.springdatajpa.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class CommentNotificationService {

    private static final String POST_COMMENT_MESSAGE =
            "%s님이 회원님의 게시글에 댓글을 남겼습니다.";
    private static final String COMMENT_REPLY_MESSAGE =
            "%s님이 회원님의 댓글에 답글을 남겼습니다.";
    private static final String REPLY_REPLY_MESSAGE =
            "%s님이 회원님의 답글에 답글을 남겼습니다.";

    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void createForNewComment(Post post, Comment comment) {
        User actor = comment.getAuthor();
        User recipient = post.getAuthor();
        if (isSelfNotification(actor, recipient)) {
            return;
        }

        saveAndPublish(Notification.comment(
                recipient,
                actor,
                NotificationType.POST_COMMENT,
                post,
                comment,
                POST_COMMENT_MESSAGE.formatted(actor.getNickname())
        ));
    }

    public void createForNewReply(Post post, Comment reply, Comment replyTo) {
        User actor = reply.getAuthor();
        User recipient = replyTo.getAuthor();
        if (isSelfNotification(actor, recipient)) {
            return;
        }

        boolean replyingToRootComment = replyTo.getParent() == null;
        NotificationType notificationType = replyingToRootComment
                ? NotificationType.COMMENT_REPLY
                : NotificationType.REPLY_REPLY;
        String message = replyingToRootComment
                ? COMMENT_REPLY_MESSAGE.formatted(actor.getNickname())
                : REPLY_REPLY_MESSAGE.formatted(actor.getNickname());

        saveAndPublish(Notification.comment(
                recipient,
                actor,
                notificationType,
                post,
                reply,
                message
        ));
    }

    private void saveAndPublish(Notification notification) {
        Notification savedNotification = notificationRepository.save(notification);
        eventPublisher.publishEvent(NotificationCreatedEvent.from(savedNotification));
    }

    private boolean isSelfNotification(User actor, User recipient) {
        return Objects.equals(actor.getId(), recipient.getId());
    }
}
