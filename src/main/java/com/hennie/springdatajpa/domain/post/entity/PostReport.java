package com.hennie.springdatajpa.domain.post.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostReport {

    private Long id;
    private Long postId;
    private Long reporterId;
    private LocalDateTime createdAt;

    public PostReport(Long postId, Long reporterId) {
        this.postId = postId;
        this.reporterId = reporterId;
        this.createdAt = LocalDateTime.now();
    }

    public void assignId(Long id) {
        if (this.id == null) {
            this.id = id;
        }
    }
}
