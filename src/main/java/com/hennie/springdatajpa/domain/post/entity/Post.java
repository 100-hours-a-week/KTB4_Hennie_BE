package com.hennie.springdatajpa.domain.post.entity;

import com.hennie.springdatajpa.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@Getter
public class Post {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(length = 26, nullable = false)
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

    // 1 게시글: N 이미지 (양방향)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<PostImage> images = new ArrayList<>();

    public Post(String title, String content, User author, PostStatus status) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.status = status;
    }

    public Post() {

    }

    // 이미지 전체 교체 (orphanRemoval로 기존 이미지 삭제 후 새 목록 등록)
    public void replaceImages(List<String> urls) {
        this.images.clear();
        if (urls != null) {
            for (String url : urls) {
                this.images.add(new PostImage(this, url, this.images.size()));
            }
        }
    }

    public List<String> getImageUrls() {
        return images.stream().map(PostImage::getUrl).toList();
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

    public void update(String title, String content, List<String> imageUrls) {
        this.title = title;
        this.content = content;
        replaceImages(imageUrls);
        markEdited();
    }

    public void updateDraft(String title, String content, List<String> imageUrls) {
        if (title != null) {
            this.title = title;
        }

        if (content != null) {
            this.content = content;
        }

        if (imageUrls != null) {
            replaceImages(imageUrls);
        }
    }

    public void publish(String title, String content, List<String> imageUrls) {
        this.title = title;
        this.content = content;
        replaceImages(imageUrls);
        this.status = PostStatus.PUBLISHED;
    }
}
