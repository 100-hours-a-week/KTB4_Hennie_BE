package com.hennie.springdatajpa.domain.techarticle.controller;

import com.hennie.springdatajpa.domain.techarticle.dto.response.TechArticleListResponseDto;
import com.hennie.springdatajpa.domain.techarticle.service.TechArticleQueryService;
import com.hennie.springdatajpa.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tech-articles")
@RequiredArgsConstructor
public class TechArticleController {

    private final TechArticleQueryService techArticleQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<TechArticleListResponseDto>> getTechArticles(
            @RequestParam(required = false) String enterprise,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        TechArticleListResponseDto result = techArticleQueryService
                .getTechArticles(enterprise, page, size);

        return ResponseEntity.ok(
                ApiResponse.of("getTechArticlesSuccess", result)
        );
    }
}
