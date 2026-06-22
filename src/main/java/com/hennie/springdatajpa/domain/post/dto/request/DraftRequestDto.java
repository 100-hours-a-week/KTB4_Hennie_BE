package com.hennie.springdatajpa.domain.post.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DraftRequestDto {
    private String title;
    private String content;
    private List<String> images;
}
