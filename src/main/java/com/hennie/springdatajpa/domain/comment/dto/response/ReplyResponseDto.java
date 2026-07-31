package com.hennie.springdatajpa.domain.comment.dto.response;

import com.hennie.springdatajpa.domain.comment.entity.Comment;
import lombok.Getter;

@Getter
public class ReplyResponseDto {
    private Long replyId;
    private Long authorId;
    private String nickname;
    private String profileUrl;
    private ReplyTargetResponseDto replyTo;
    private String content;
    private String createdAt;
    private boolean isDeleted;
    private boolean isEdited;

    // 대댓글은 부모를 가진 Comment(자기참조)
    public ReplyResponseDto(Comment reply) {
        this.replyId = reply.getId();
        this.authorId = reply.isDeleted() || reply.getAuthor().isAuthorDeleted()
                ? null
                : reply.getAuthor().getId();
        this.nickname = reply.isDeleted()
                ? null
                : reply.getAuthor().isAuthorDeleted()
                    ? "알 수 없음"
                    : reply.getAuthor().getNickname();
        this.profileUrl = reply.isDeleted() || reply.getAuthor().isAuthorDeleted()
                ? null
                : reply.getAuthor().getProfileUrl();
        this.replyTo = reply.getReplyTo() == null
                ? null
                : new ReplyTargetResponseDto(reply.getReplyTo());
        this.content = reply.getContent();
        this.createdAt = reply.getFormattedCreatedAt();
        this.isDeleted = reply.isDeleted();
        this.isEdited = reply.isEdited();
    }
}
