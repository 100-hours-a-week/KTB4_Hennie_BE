package com.hennie.springdatajpa.domain.post.entity;

import com.hennie.springdatajpa.domain.techarticle.entity.TechArticle;
import com.hennie.springdatajpa.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(
        name = "post",
        indexes = @Index(name = "post_tech_article_id", columnList = "tech_article_id")
)
@Getter
public class Post {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    private int viewCount;
    private int reportCount;
    private boolean blinded;
    private boolean edited;

    // 1 사용자: N 게시글 (양방향)
    // 게시글은 작성자에 대한 외래 키를 가짐
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // FK
    private User author;

    // N 게시글: 1 기술 아티클 (단방향)
    // 일반 사용자 게시글은 원문 없이 존재할 수 있으므로 FK는 nullable이다.
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(
            name = "tech_article_id",
            nullable = true,
            foreignKey = @ForeignKey(name = "fk_post_tech_article")
    )
    private TechArticle techArticle;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private PostCategory category;

    public Post(String title, String content, User author, PostStatus status, PostCategory category) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.status = status;
        this.category = category;
    }

    public Post() {

    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void report() {
        this.reportCount++;
        if (this.reportCount >= 5) {
            this.blinded = true;
        }
    }

    public String getFormattedCreatedAt() {
        return createdAt.format(DATE_TIME_FORMATTER);
    }

    public String getFormattedModifiedAt() {
        return modifiedAt.format(DATE_TIME_FORMATTER);
    }

    private void markEdited() {
        this.edited = true;
    }

    // 부분 수정: null로 들어온 필드는 기존 값 유지
    public void update(String title, String content, PostCategory category) {
        updateFields(title, content, category);
        markEdited();
    }

    public void updateDraft(String title, String content, PostCategory category) {
        updateFields(title, content, category);
    }

    private void updateFields(String title, String content, PostCategory category) {
        if (title != null) {
            this.title = title;
        }

        if (content != null) {
            this.content = content;
        }

        if (category != null) {
            this.category = category;
        }
    }

    public void publish(String title, String content, PostCategory category) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.status = PostStatus.PUBLISHED;
    }
}
