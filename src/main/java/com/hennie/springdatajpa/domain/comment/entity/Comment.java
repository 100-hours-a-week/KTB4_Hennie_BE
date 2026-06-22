package com.hennie.springdatajpa.domain.comment.entity;

import com.hennie.springdatajpa.domain.post.entity.Post;
import com.hennie.springdatajpa.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comment")
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // N:1 - 여러 댓글이 한 게시글에 속함
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // FK

    @ManyToOne(fetch = FetchType.LAZY) // N:1 - 여러 댓글이 한 사용자에 속함
    @JoinColumn(name = "user_id", nullable = false)
    private User author; // FK
    private String content;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    private boolean deleted;

    // 자기참조: parent == null 이면 댓글, not null 이면 대댓글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    // 1 댓글: N 대댓글 (양방향)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    public Comment(Post post, User author, String content) {
        this.post = post;
        this.author = author;
        this.content = deleted ? "삭제된 댓글입니다" : content;
    }

    public Comment() {

    }

    public void update(String content) {
        this.content = content;
    }

    public void delete() {
        this.content = "삭제된 댓글입니다";
        this.deleted = true;
    }

    public String getFormattedCreatedAt() {
        return createdAt.format(Post.DATE_TIME_FORMATTER);
    }
}
