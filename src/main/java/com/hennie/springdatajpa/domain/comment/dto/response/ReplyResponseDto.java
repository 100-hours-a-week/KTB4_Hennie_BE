package com.hennie.springdatajpa.domain.comment.dto.response;

import com.hennie.springdatajpa.domain.comment.entity.Comment;
import lombok.Getter;

@Getter
public class ReplyResponseDto {
    private Long replyId;
    private String nickname;
    private String content;
    private String createdAt;
    private boolean isDeleted;

    // 대댓글은 부모를 가진 Comment(자기참조)
    public ReplyResponseDto(Comment reply) {
        this.replyId = reply.getId();
        this.nickname = reply.isDeleted()
                ? null
                : reply.getAuthor().isAuthorDeleted()
                    ? "알 수 없음"
                    : reply.getAuthor().getNickname();
        this.content = reply.getContent();
        this.createdAt = reply.getFormattedCreatedAt();
        this.isDeleted = reply.isDeleted();
    }
}
