package com.hennie.springdatajpa.domain.post.dto.response;

import com.hennie.springdatajpa.domain.post.entity.Post;
import lombok.Getter;

@Getter
public class PostReportResponseDto {

    private Long postId;
    private int reportCount;
    private boolean isBlinded;

    public PostReportResponseDto(Post post) {
        this.postId = post.getId();
        this.reportCount = post.getReportCount();
        this.isBlinded = post.isBlinded();
    }
}
