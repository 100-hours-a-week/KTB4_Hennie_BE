package com.hennie.springdatajpa.domain.post.controller;

import com.hennie.springdatajpa.domain.post.dto.request.DraftRequestDto;
import com.hennie.springdatajpa.domain.post.dto.request.PostRequestDto;
import com.hennie.springdatajpa.domain.post.dto.response.*;
import com.hennie.springdatajpa.domain.post.service.PostService;
import com.hennie.springdatajpa.domain.report.dto.request.ReportRequestDto;
import com.hennie.springdatajpa.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // 게시글 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<PostListResponseDto>> getPublishedPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PostListResponseDto result = postService.getPublishedPosts(page, size);
        return ResponseEntity.ok(
                ApiResponse.of("GET_POSTS_SUCCESS", result)
        );
    }

    // 게시글 발행
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PostRequestDto request
    ) {
        PostResponseDto result = postService.createPost(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("POST_CREATED", result));
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponseDto>> getPost(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId
    ) {
        PostDetailResponseDto result = postService.getPost(userId, postId);

        return ResponseEntity.ok(
                ApiResponse.of("GET_POST_SUCCESS", result)
        );
    }

    // 게시글 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> updatePost(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody PostRequestDto request
    ) {
        PostResponseDto result = postService.updatePost(userId, postId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("UPDATE_POST_SUCCESS", result));
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId
    ) {
        postService.deletePost(userId, postId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("DELETE_POST_SUCCESS", null));
    }

    // 게시글 신고
    @PostMapping("/{postId}/reports")
    public ResponseEntity<ApiResponse<PostReportResponseDto>> reportPost(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody ReportRequestDto request
    ) {
        PostReportResponseDto result = postService.reportPost(userId, postId, request.getReason());
        return ResponseEntity.ok(
                ApiResponse.of("REPORT_POST_SUCCESS", result)
        );
    }

    // 게시글 처음 임시저장
    @PostMapping("/drafts")
    public ResponseEntity<ApiResponse<DraftResponseDto>> createDraftPost(
            @AuthenticationPrincipal Long userId,
            @RequestBody DraftRequestDto request
    ) {
        DraftResponseDto result = postService.createDraftPost(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("CREATE_DRAFT_SUCCESS", result));
    }

    // 임시저장된 게시글 목록 조회
    @GetMapping("/drafts")
    public ResponseEntity<ApiResponse<DraftListResponseDto>> getDraftPosts(
            @AuthenticationPrincipal Long userId
    ) {
        DraftListResponseDto result = postService.getDraftPosts(userId);
        return ResponseEntity.ok(
                ApiResponse.of("GET_DRAFTS_SUCCESS", result)
        );
    }

    // 임시저장된 게시글 상세 조회 (for 수정하기 위함)
    @GetMapping("/drafts/{postId}")
    public ResponseEntity<ApiResponse<DraftResponseDto>> getDraftPost(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId
    ) {
        DraftResponseDto result = postService.getDraftPost(userId, postId);
        return ResponseEntity.ok(
                ApiResponse.of("GET_DRAFT_SUCCESS", result)
        );
    }

    // 게시글 재임시저장
    @PutMapping("/drafts/{postId}")
    public ResponseEntity<ApiResponse<DraftResponseDto>> updateDraftPost(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId,
            @RequestBody DraftRequestDto request
    ) {
        DraftResponseDto result = postService.updateDraftPost(userId, postId, request);
        return ResponseEntity.ok(
                ApiResponse.of("UPDATE_DRAFT_SUCCESS", result)
        );
    }

    // 임시저장된 게시글 삭제
    @DeleteMapping("/drafts/{postId}")
    public ResponseEntity<ApiResponse<Void>> deleteDraftPost(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId
    ) {
        postService.deleteDraftPost(userId, postId);
        return ResponseEntity.ok(
                ApiResponse.of("DELETE_DRAFT_SUCCESS", null)
        );
    }
}
