package com.hennie.springdatajpa.domain.like.controller;

import com.hennie.springdatajpa.domain.like.dto.response.PostLikeResponseDto;
import com.hennie.springdatajpa.domain.like.service.PostLikeService;
import com.hennie.springdatajpa.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts/{postId}/likes")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    // 좋아요
    @PostMapping
    public ResponseEntity<ApiResponse<PostLikeResponseDto>> likePost(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId
    ) {
        PostLikeResponseDto result = postLikeService.likePost(userId, postId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("POST_LIKED", result));
    }

    // 좋아요 취소
    @DeleteMapping
    public ResponseEntity<ApiResponse<PostLikeResponseDto>> unlikePost(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId
    ) {
        PostLikeResponseDto result = postLikeService.unlikePost(userId, postId);
        return ResponseEntity.ok(
                ApiResponse.of("POST_UNLIKED", result)
        );
    }
}
