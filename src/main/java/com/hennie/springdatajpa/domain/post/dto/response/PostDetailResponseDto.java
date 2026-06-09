package com.hennie.springdatajpa.domain.post.dto.response;

import com.hennie.springdatajpa.domain.comment.dto.response.CommentResponseDto;
import com.hennie.springdatajpa.domain.comment.entity.Comment;
import com.hennie.springdatajpa.domain.post.entity.Post;
import com.hennie.springdatajpa.domain.post.entity.PostStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class PostDetailResponseDto {
    private Long postId;
    private String title;
    private String content;
    private String image;
    private String nickname;
    private String createdAt;
    private String modifiedAt;
    private int viewCount;
    private int likeCount;
    private boolean isLiked;
    private int commentCount;
    private List<CommentResponseDto> comments;
    private PostStatus status;
    private boolean isEdited;
    private int reportCount;
    private boolean isBlinded;

    public PostDetailResponseDto(
            Post post,
            int likeCount,
            boolean liked,
            int commentCount,
            List<Comment> comments
    ) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.image = post.getImage();
        this.nickname = post.getAuthor().getNickname();;
        this.createdAt = post.getFormattedCreatedAt();
        this.modifiedAt = post.getFormattedModifiedAt();
        this.viewCount = post.getViewCount();
        this.likeCount = likeCount;
        this.isLiked = liked;
        this.commentCount = commentCount;
        this.comments = comments.stream()
                .map(CommentResponseDto::new)
                .toList();
        this.status = post.getStatus();
        this.isEdited = post.isEdited();
        this.reportCount = post.getReportCount();
        this.isBlinded = post.isBlinded();
    }
}
