package com.hennie.springdatajpa.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReplyCreateRequestDto {

    @NotBlank(message = "대댓글 내용은 필수값입니다.")
    private String content;

    private Long replyToId;
}
