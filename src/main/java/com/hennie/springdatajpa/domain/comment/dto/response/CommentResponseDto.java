package com.hennie.springdatajpa.domain.comment.dto.response;

import com.hennie.springdatajpa.domain.comment.entity.Comment;
import lombok.Getter;

import java.util.List;

@Getter
public class CommentResponseDto {
    private Long commentId;
    private String nickname;
    private String content;
    private String createdAt;
    private boolean isDeleted;
    private List<ReplyResponseDto> replies;

    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getId();
        this.nickname = comment.isDeleted()
                ? null
                : comment.getAuthor().isAuthorDeleted()
                    ? "알 수 없음"
                    : comment.getAuthor().getNickname();
        this.content = comment.getContent();
        this.createdAt = comment.getFormattedCreatedAt();
        this.isDeleted = comment.isDeleted();
        this.replies = comment.getChildren().stream()
                .map(ReplyResponseDto::new)
                .toList();
    }
}
