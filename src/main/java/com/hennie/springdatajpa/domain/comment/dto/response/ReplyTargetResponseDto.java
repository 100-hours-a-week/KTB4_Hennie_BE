package com.hennie.springdatajpa.domain.comment.dto.response;

import com.hennie.springdatajpa.domain.comment.entity.Comment;
import lombok.Getter;

@Getter
public class ReplyTargetResponseDto {

    private final Long commentId;
    private final Long authorId;
    private final String nickname;
    private final boolean deleted;

    public ReplyTargetResponseDto(Comment target) {
        boolean authorDeleted = target.getAuthor().isAuthorDeleted();

        this.commentId = target.getId();
        this.authorId = authorDeleted ? null : target.getAuthor().getId();
        this.nickname = authorDeleted ? "알 수 없음" : target.getAuthor().getNickname();
        this.deleted = target.isDeleted();
    }
}
