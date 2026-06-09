package com.hennie.springdatajpa.domain.post.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PostListResponseDto {

    private List<PostListItemResponseDto> posts;
    private int page;
    private int size;
    private long totalCount;
    private int totalPages;
    private boolean hasNext;

    public PostListResponseDto(
            List<PostListItemResponseDto> posts,
            int page,
            int size,
            long totalCount,
            int totalPages,
            boolean hasNext
    ) {
        this.posts = posts;
        this.page = page;
        this.size = size;
        this.totalCount = totalCount;
        this.totalPages = totalPages;
        this.hasNext = hasNext;
    }
}
