package com.hennie.springdatajpa.global.store;

import com.hennie.springdatajpa.domain.comment.entity.Comment;
import com.hennie.springdatajpa.domain.comment.entity.Reply;
import com.hennie.springdatajpa.domain.like.entity.PostLike;
import com.hennie.springdatajpa.domain.post.entity.Post;
import com.hennie.springdatajpa.domain.post.entity.PostEditHistory;
import com.hennie.springdatajpa.domain.post.entity.PostReport;
import com.hennie.springdatajpa.domain.post.entity.PostStatus;
import com.hennie.springdatajpa.domain.user.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// DB 없이 Map 기반으로 구현
@Getter
@Component
public class InMemoryDataStore {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<Long, Post> posts = new ConcurrentHashMap<>();
    private final Map<Long, Comment> comments = new ConcurrentHashMap<>();
    private final Map<Long, PostLike> postLikes = new ConcurrentHashMap<>();
    private final Map<Long, PostEditHistory> postEditHistories = new ConcurrentHashMap<>();
    private final Map<Long, PostReport> postReports = new ConcurrentHashMap<>();
    private final AtomicLong userSequence = new AtomicLong(1);
    private final AtomicLong postSequence = new AtomicLong(1);
    private final AtomicLong commentSequence = new AtomicLong(1);
    private final AtomicLong postLikeSequence = new AtomicLong(1);
    private final AtomicLong postEditHistorySequence = new AtomicLong(1);
    private final AtomicLong postReportSequence = new AtomicLong(1);

    @PostConstruct
    public void init() {
        User user = new User(
                "test@example.com",
                "password123",
                "tester",
                "https://example.com/profile.png"
        );
        user.assignId(nextUserId());
        users.put(user.getId(), user);

        User commenter = new User(
                "commenter@example.com",
                "password123",
                "hennie",
                "https://example.com/commenter.png"
        );
        commenter.assignId(nextUserId());
        users.put(commenter.getId(), commenter);

        Post post = new Post(
                "First post",
                "This post is stored in memory without a database.",
                null,
                user,
                PostStatus.PUBLISHED
        );
        post.assignId(nextPostId());
        posts.put(post.getId(), post);

        PostLike postLike = new PostLike(post.getId(), user.getId());
        postLike.assignId(nextPostLikeId());
        postLikes.put(postLike.getId(), postLike);

        Comment comment = new Comment(post.getId(), commenter, "test comment content", false);
        comment.assignId(nextCommentId());
        comments.put(comment.getId(), comment);

        Comment deletedComment = new Comment(post.getId(), commenter, "deleted comment", true);
        deletedComment.assignId(nextCommentId());
        deletedComment.addReply(new Reply(10L, commenter, "test reply content", false));
        comments.put(deletedComment.getId(), deletedComment);
    }

    public Long nextUserId() {
        return userSequence.getAndIncrement();
    }

    public Long nextPostId() {
        return postSequence.getAndIncrement();
    }

    public Long nextCommentId() {
        return commentSequence.getAndIncrement();
    }

    public Long nextPostLikeId() {
        return postLikeSequence.getAndIncrement();
    }

    public Long nextPostEditHistoryId() {
        return postEditHistorySequence.getAndIncrement();
    }

    public Long nextPostReportId() {
        return postReportSequence.getAndIncrement();
    }
}
