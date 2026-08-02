package com.hennie.springdatajpa.domain.post.dto.response;

import lombok.Getter;
import com.hennie.springdatajpa.domain.post.entity.Post;
import com.hennie.springdatajpa.domain.post.entity.PostCategory;
import com.hennie.springdatajpa.domain.post.entity.PostStatus;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private PostCategory category;
    private Long authorId;
    private PostStatus status;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.category = post.getCategory();
        this.authorId = post.getAuthor().getId();
        this.status = post.getStatus();
    }
}
