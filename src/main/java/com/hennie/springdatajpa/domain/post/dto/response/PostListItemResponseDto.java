package com.hennie.springdatajpa.domain.post.dto.response;

import com.hennie.springdatajpa.domain.post.entity.Post;
import com.hennie.springdatajpa.domain.post.entity.PostCategory;
import lombok.Getter;

@Getter
public class PostListItemResponseDto {

    private Long postId;
    private String title;
    private PostCategory category; // 목록의 썸네일도 이 값으로 정해진다.
    private String nickname;
    private int likeCount;
    private int commentCount;
    private int viewCount;
    private String createdAt;
    private String profileUrl;

    public PostListItemResponseDto(Post post, int likeCount, int commentCount) {
        this.postId = post.getId();
        this.title = post.isBlinded() ? "숨김 처리된 게시글" : post.getTitle();
        this.category = post.getCategory();
        this.nickname = post.getAuthor().isAuthorDeleted() ? "알 수 없음" : post.getAuthor().getNickname();
        this.profileUrl = post.getAuthor().isAuthorDeleted() ? null : post.getAuthor().getProfileUrl();
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.viewCount = post.getViewCount();
        this.createdAt = post.getFormattedCreatedAt();

    }
}
