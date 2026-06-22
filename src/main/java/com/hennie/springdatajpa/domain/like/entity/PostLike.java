package com.hennie.springdatajpa.domain.like.entity;

import com.hennie.springdatajpa.domain.post.entity.Post;
import com.hennie.springdatajpa.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "post_likes",
        uniqueConstraints = {
                // 같은 사용자가 같은 게시글에 좋아요를 중복으로 누르지 못하도록
                @UniqueConstraint(name = "uq_post_like", columnNames = {"post_id", "user_id"})
        }
)
@Getter
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_likes_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // N:1 - 여러 좋아요가 한 게시글에 속함
    @JoinColumn(name = "post_id", nullable = false) // FK
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY) // N:1 - 여러 좋아요가 한 사용자에 속함
    @JoinColumn(name = "user_id", nullable = false) // FK
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public PostLike(Post post, User user) {
        this.post = post;
        this.user = user;
    }

    public PostLike() {

    }
}
