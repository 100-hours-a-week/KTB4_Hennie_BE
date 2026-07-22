package com.hennie.springdatajpa.domain.post.dto.response;

import com.hennie.springdatajpa.domain.post.entity.Post;
import com.hennie.springdatajpa.domain.post.entity.PostStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class DraftResponseDto {
    private Long postId;
    private Long authorId;
    private String title;
    private String content;
    private List<String> images;
    private String createdAt;
    private String modifiedAt;
    private PostStatus status;

    public DraftResponseDto(Post post) {
        this.postId = post.getId();
        this.authorId = post.getAuthor().getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.images = post.getImageUrls();
        this.createdAt = post.getFormattedCreatedAt();
        this.modifiedAt = post.getFormattedModifiedAt();
        this.status = post.getStatus();
    }
}
