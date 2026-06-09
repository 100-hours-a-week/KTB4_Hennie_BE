package com.hennie.springdatajpa.domain.post.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostEditHistory {

    private Long id;
    private Long postId;
    private Long editorId;
    private String beforeTitle;
    private String beforeContent;
    private String beforeImage;
    private String afterTitle;
    private String afterContent;
    private String afterImage;
    private LocalDateTime editedAt;

    public PostEditHistory(
            Long postId,
            Long editorId,
            String beforeTitle,
            String beforeContent,
            String beforeImage,
            String afterTitle,
            String afterContent,
            String afterImage
    ) {
        this.postId = postId;
        this.editorId = editorId;
        this.beforeTitle = beforeTitle;
        this.beforeContent = beforeContent;
        this.beforeImage = beforeImage;
        this.afterTitle = afterTitle;
        this.afterContent = afterContent;
        this.afterImage = afterImage;
        this.editedAt = LocalDateTime.now();
    }

    public void assignId(Long id) {
        if (this.id == null) {
            this.id = id;
        }
    }
}
