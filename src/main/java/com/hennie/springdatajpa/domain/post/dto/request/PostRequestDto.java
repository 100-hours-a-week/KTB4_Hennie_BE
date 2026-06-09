package com.hennie.springdatajpa.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    private Long postId;

    @NotBlank(message = "제목은 필수값입니다.")
    private String title;

    @NotBlank(message = "내용은 필수값입니다.")
    private String content;

    private String image;
}
