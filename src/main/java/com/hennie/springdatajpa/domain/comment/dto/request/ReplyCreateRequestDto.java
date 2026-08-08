package com.hennie.springdatajpa.domain.comment.dto.request;

import com.hennie.springdatajpa.domain.comment.entity.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReplyCreateRequestDto {

    @NotBlank(message = "대댓글 내용은 필수값입니다.")
    @Size(max = Comment.CONTENT_MAX_LENGTH, message = "대댓글은 3000자 이하여야 합니다.")
    private String content;

    private Long replyToId;
}
