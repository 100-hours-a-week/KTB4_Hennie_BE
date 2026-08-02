package com.hennie.springdatajpa.domain.post.dto.request;

import com.hennie.springdatajpa.domain.post.entity.PostCategory;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostUpdateRequestDto {

    @Size(max = 26, message = "제목은 26자 이하여야 합니다.")
    private String title;

    private String content;

    private PostCategory category;

    public boolean hasNoField() {
        return title == null && content == null && category == null;
    }
}
