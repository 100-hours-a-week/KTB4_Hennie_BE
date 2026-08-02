package com.hennie.springdatajpa.domain.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_edit_history")
@Getter
public class PostEditHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_edit_hist_id")
    private Long id;
    private Long postId;
    private Long editorId;
    private String beforeTitle;

    @Column(columnDefinition = "TEXT")
    private String beforeContent;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private PostCategory beforeCategory;
    private String afterTitle;

    @Column(columnDefinition = "TEXT")
    private String afterContent;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private PostCategory afterCategory;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime editedAt;

    public PostEditHistory(
            Long postId,
            Long editorId,
            String beforeTitle,
            String beforeContent,
            PostCategory beforeCategory,
            String afterTitle,
            String afterContent,
            PostCategory afterCategory
    ) {
        this.postId = postId;
        this.editorId = editorId;
        this.beforeTitle = beforeTitle;
        this.beforeContent = beforeContent;
        this.beforeCategory = beforeCategory;
        this.afterTitle = afterTitle;
        this.afterContent = afterContent;
        this.afterCategory = afterCategory;
    }

    public PostEditHistory() {

    }
}
