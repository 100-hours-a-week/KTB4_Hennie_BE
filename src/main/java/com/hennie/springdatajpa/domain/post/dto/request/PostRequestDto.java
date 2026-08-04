package com.hennie.springdatajpa.domain.post.dto.request;

import com.hennie.springdatajpa.domain.post.entity.PostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    private Long postId;

    @Size(max = 100)
    @NotBlank(message = "제목은 필수값입니다.")
    private String title;

    @NotBlank(message = "내용은 필수값입니다.")
    private String content;

    @NotNull(message = "유형은 필수값입니다.")
    private PostCategory category;
}
