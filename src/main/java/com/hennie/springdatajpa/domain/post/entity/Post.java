package com.hennie.springdatajpa.domain.post.entity;

import com.hennie.springdatajpa.domain.user.entity.User;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

// DB 사용 시:
// @Entity
@Getter
// @RequiredArgsConstructor
public class Post {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(name = "post_id")
    private Long id;
    private String title;
    private String content;
    private String image;
    private PostStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int viewCount;
    private int reportCount;
    private boolean blinded;
    private boolean edited;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id")
    private User author;

    public Post(String title, String content, String image, User author, PostStatus status) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.author = author;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = this.createdAt;
    }

    public void assignId(Long id) {
        if (this.id == null) {
            this.id = id;
        }
    }

    public void anonymizeAuthor() {
        this.author = null;
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
        this.modifiedAt = LocalDateTime.now();
    }

    public void update(String title, String content, String image) {
        this.title = title;
        this.content = content;
        this.image = image;
        markEdited();
    }

    public void updateDraft(String title, String content, String image) {
        boolean updated = false;

        if (title != null) {
            this.title = title;
            updated = true;
        }

        if (content != null) {
            this.content = content;
            updated = true;
        }

        if (image != null) {
            this.image = image;
            updated = true;
        }

        if (updated) {
            this.modifiedAt = LocalDateTime.now();
        }
    }

    public void publish(String title, String content, String image) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.status = PostStatus.PUBLISHED;
        this.modifiedAt = LocalDateTime.now();
    }
}
