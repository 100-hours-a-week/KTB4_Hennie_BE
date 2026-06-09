package com.hennie.springdatajpa.domain.post.dto.response;

import com.hennie.springdatajpa.domain.post.entity.Post;
import com.hennie.springdatajpa.domain.post.entity.PostStatus;
import lombok.Getter;

@Getter
public class DraftResponseDto {
    private Long postId;
    private Long authorId;
    private String title;
    private String content;
    private String image;
    private String modifiedAt;
    private PostStatus status;

    public DraftResponseDto(Post post) {
        this.postId = post.getId();
        this.authorId = post.getAuthor().getId();;
        this.title = post.getTitle();
        this.content = post.getContent();
        this.image = post.getImage();
        this.modifiedAt = post.getFormattedModifiedAt();
        this.status = post.getStatus();
    }
}
