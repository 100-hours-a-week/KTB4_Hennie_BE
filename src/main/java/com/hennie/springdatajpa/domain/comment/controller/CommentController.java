package com.hennie.springdatajpa.domain.comment.controller;

import com.hennie.springdatajpa.domain.comment.dto.request.CommentRequestDto;
import com.hennie.springdatajpa.domain.comment.dto.response.CommentResponseDto;
import com.hennie.springdatajpa.domain.comment.service.CommentService;
import com.hennie.springdatajpa.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequestDto request
    ) {
        CommentResponseDto result = commentService.createComment(userId, postId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("COMMENT_CREATED", result));
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequestDto request
    ) {
        CommentResponseDto result = commentService.updateComment(userId, postId, commentId, request);
        return ResponseEntity.ok(
                ApiResponse.of("COMMENT_UPDATED", result)
        );
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(userId, postId, commentId);
        return ResponseEntity.ok(
                ApiResponse.of("COMMENT_DELETED", null)
        );
    }
}
