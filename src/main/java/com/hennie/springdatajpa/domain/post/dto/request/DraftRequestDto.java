package com.hennie.springdatajpa.domain.post.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DraftRequestDto {
    private String title;
    private String content;
    private String image;
}
