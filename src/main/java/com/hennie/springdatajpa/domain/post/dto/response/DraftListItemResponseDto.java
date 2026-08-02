package com.hennie.springdatajpa.domain.post.dto.response;

import com.hennie.springdatajpa.domain.post.entity.Post;
import com.hennie.springdatajpa.domain.post.entity.PostCategory;
import com.hennie.springdatajpa.domain.post.entity.PostStatus;
import lombok.Getter;

@Getter
public class DraftListItemResponseDto {
    private Long postId;
    private String title;
    private String content;
    private PostCategory category;
    private String createdAt;
    private String modifiedAt;
    private PostStatus status;

    public DraftListItemResponseDto(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.category = post.getCategory();
        this.createdAt = post.getFormattedCreatedAt();
        this.modifiedAt = post.getFormattedModifiedAt();
        this.status = post.getStatus();
    }
}
