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

    @Column(length = 1024)
    private String beforeImageUrl;
    private String afterTitle;

    @Column(columnDefinition = "TEXT")
    private String afterContent;

    @Column(length = 1024)
    private String afterImageUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime editedAt;

    public PostEditHistory(
            Long postId,
            Long editorId,
            String beforeTitle,
            String beforeContent,
            String beforeImageUrl,
            String afterTitle,
            String afterContent,
            String afterImageUrl
    ) {
        this.postId = postId;
        this.editorId = editorId;
        this.beforeTitle = beforeTitle;
        this.beforeContent = beforeContent;
        this.beforeImageUrl = beforeImageUrl;
        this.afterTitle = afterTitle;
        this.afterContent = afterContent;
        this.afterImageUrl = afterImageUrl;
    }

    public PostEditHistory() {

    }
}
