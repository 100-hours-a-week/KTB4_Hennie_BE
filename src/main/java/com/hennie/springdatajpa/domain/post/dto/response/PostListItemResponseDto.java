package com.hennie.springdatajpa.domain.post.dto.response;

import com.hennie.springdatajpa.domain.post.entity.Post;
import lombok.Getter;

@Getter
public class PostListItemResponseDto {

    private Long postId;
    private String title;
    private String nickname;
    private int likeCount;
    private int commentCount;
    private int viewCount;
    private String createdAt;

    public PostListItemResponseDto(Post post, int likeCount, int commentCount) {
        this.postId = post.getId();
        this.title = post.isBlinded() ? "숨김 처리된 게시글" : post.getTitle();
        this.nickname = post.getAuthor() == null ? "알 수 없음" : post.getAuthor().getNickname();
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.viewCount = post.getViewCount();
        this.createdAt = post.getFormattedCreatedAt();
    }
}
