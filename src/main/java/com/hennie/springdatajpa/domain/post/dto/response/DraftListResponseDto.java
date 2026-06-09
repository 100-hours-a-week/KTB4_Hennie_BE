package com.hennie.springdatajpa.domain.post.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class DraftListResponseDto {
    private List<DraftListItemResponseDto> drafts;
    private int totalCount;

    public DraftListResponseDto(List<DraftListItemResponseDto> drafts) {
        this.drafts = drafts;
        this.totalCount = drafts.size();
    }
}
