package com.hennie.springdatajpa.domain.post.dto.response;

import lombok.Getter;
import com.hennie.springdatajpa.domain.post.entity.Post;
import com.hennie.springdatajpa.domain.post.entity.PostStatus;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String image;
    private Long authorId;
    private PostStatus status;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.image = post.getImage();
        this.authorId = post.getAuthor() == null ? null : post.getAuthor().getId();
        this.status = post.getStatus();
    }
}
