package com.hennie.springdatajpa.domain.techarticle.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class TechArticleListResponseDto {

    private final List<TechArticleListItemResponseDto> articles;
    private final int page;
    private final int size;
    private final long totalCount;
    private final int totalPages;
    private final boolean hasNext;

    public TechArticleListResponseDto(
            List<TechArticleListItemResponseDto> articles,
            int page,
            int size,
            long totalCount,
            int totalPages,
            boolean hasNext
    ) {
        this.articles = articles;
        this.page = page;
        this.size = size;
        this.totalCount = totalCount;
        this.totalPages = totalPages;
        this.hasNext = hasNext;
    }
}
