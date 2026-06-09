package com.hennie.springdatajpa.domain.post.service;

import com.hennie.springdatajpa.domain.comment.entity.Comment;
import com.hennie.springdatajpa.domain.comment.repository.CommentRepository;
import com.hennie.springdatajpa.domain.like.repository.PostLikeRepository;
import com.hennie.springdatajpa.domain.post.dto.request.DraftRequestDto;
import com.hennie.springdatajpa.domain.post.dto.request.PostRequestDto;
import com.hennie.springdatajpa.domain.post.dto.response.*;
import com.hennie.springdatajpa.domain.post.entity.Post;
import com.hennie.springdatajpa.domain.post.entity.PostEditHistory;
import com.hennie.springdatajpa.domain.post.entity.PostReport;
import com.hennie.springdatajpa.domain.post.entity.PostStatus;
import com.hennie.springdatajpa.domain.post.repository.PostReportRepository;
import com.hennie.springdatajpa.domain.post.repository.PostRepository;
import com.hennie.springdatajpa.domain.user.entity.User;
import com.hennie.springdatajpa.domain.user.repository.UserRepository;
import com.hennie.springdatajpa.global.exception.BadRequestException;
import com.hennie.springdatajpa.global.exception.DuplicateResourceException;
import com.hennie.springdatajpa.global.exception.ForbiddenException;
import com.hennie.springdatajpa.global.exception.NotFoundException;
import com.hennie.springdatajpa.global.store.InMemoryDataStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostReportRepository postReportRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final InMemoryDataStore store;

    // @Transactional(readOnly = true)
    // 게시글 목록 조회
    public PostListResponseDto getPublishedPosts(int page, int size) {
        if (page < 1 || size < 1) {
            throw new BadRequestException("INVALID_PAGE_REQUEST");
        }

        List<Post> publishedPosts = postRepository.findAll().stream()
                .filter(post -> post.getStatus() == PostStatus.PUBLISHED)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();

        long totalCount = publishedPosts.size();
        int totalPages = (int) Math.ceil((double) totalCount / size);
        int fromIndex = Math.min((page - 1) * size, publishedPosts.size());
        int toIndex = Math.min(fromIndex + size, publishedPosts.size());

        List<PostListItemResponseDto> posts = publishedPosts.subList(fromIndex, toIndex).stream()
                .map(post -> new PostListItemResponseDto(
                        post,
                        Math.toIntExact(postLikeRepository.countByPostId(post.getId())),
                        Math.toIntExact(commentRepository.countByPostId(post.getId()))
                ))
                .toList();

        boolean hasNext = page < totalPages;

        return new PostListResponseDto(posts, page, size, totalCount, totalPages, hasNext);
    }

    // @Transactional
    // 게시글 발행
    public PostResponseDto createPost(Long userId, PostRequestDto request) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        // 임시 저장된 게시글 -> 발행할 때
        if (request.getPostId() != null) {
            return publishDraftPost(userId, request);
        }

        Post post = new Post(
                request.getTitle(),
                request.getContent(),
                request.getImage(),
                author,
                PostStatus.PUBLISHED
        );

        Post savedPost = postRepository.save(post);
        return new PostResponseDto(savedPost);
    }

    private PostResponseDto publishDraftPost(Long userId, PostRequestDto request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        if (post.getAuthor() == null || !post.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN");
        }

        if (post.getStatus() != PostStatus.DRAFT) {
            throw new BadRequestException("NOT_DRAFT_POST");
        }

        post.publish(request.getTitle(), request.getContent(), request.getImage());
        return new PostResponseDto(post);
    }

    // @Transactional(readOnly = true)
    // 게시글 상세 조회
    public PostDetailResponseDto getPost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        if (post.getStatus() != PostStatus.PUBLISHED) {
            throw new ForbiddenException("FORBIDDEN");
        }

        if (post.isBlinded()) {
            throw new ForbiddenException("FORBIDDEN");
        }

        post.increaseViewCount();

        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        int likeCount = Math.toIntExact(postLikeRepository.countByPostId(postId));
        boolean liked = postLikeRepository.existsByPostIdAndUserId(postId, userId);
        int commentCount = Math.toIntExact(commentRepository.countByPostId(postId));

        return new PostDetailResponseDto(post, likeCount, liked, commentCount, comments);
    }

    // @Transactional
    // 게시글 수정
    public PostResponseDto updatePost(Long userId, Long postId, PostRequestDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        if (post.getAuthor() == null || !post.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN");
        }

        if (post.getStatus() != PostStatus.PUBLISHED || post.isBlinded()) {
            throw new ForbiddenException("FORBIDDEN");
        }

        if (isNotChanged(post, request)) {
            throw new BadRequestException("noChangedValue");
        }

        // 게시글 히스토리 저장
        PostEditHistory history = new PostEditHistory(
                post.getId(),
                userId,
                post.getTitle(),
                post.getContent(),
                post.getImage(),
                request.getTitle(),
                request.getContent(),
                request.getImage()
        );
        history.assignId(store.nextPostEditHistoryId());
        store.getPostEditHistories().put(history.getId(), history);

        post.update(request.getTitle(), request.getContent(), request.getImage());

        return new PostResponseDto(post);
    }

    private boolean isNotChanged(Post post, PostRequestDto request) {
        return Objects.equals(post.getTitle(), request.getTitle())
                && Objects.equals(post.getContent(), request.getContent())
                && Objects.equals(post.getImage(), request.getImage());
    }

    // @Transactional
    // 게시글 신고
    public PostReportResponseDto reportPost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        if (postReportRepository.existsByPostIdAndReporterId(postId, userId)) {
            throw new DuplicateResourceException("ALREADY_REPORTED_POST");
        }

        PostReport postReport = new PostReport(postId, userId);
        postReportRepository.save(postReport);

        post.report();

        return new PostReportResponseDto(post);
    }

    // @Transactional
    // 게시글 삭제
    public void deletePost(Long postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));
        commentRepository.deleteById(postId);
        postLikeRepository.deleteById(postId);
        postRepository.deleteById(postId);
    }

    // @Transactional
    // 게시글 처음 임시저장
    public DraftResponseDto createDraftPost(Long userId, DraftRequestDto request) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        Post post = new Post(
                request.getTitle(),
                request.getContent(),
                request.getImage(),
                author,
                PostStatus.DRAFT
        );

        Post savedPost = postRepository.save(post);
        return new DraftResponseDto(savedPost);
    }

    // @Transactional(readOnly = true)
    // 임시저장된 게시글 목록 조회
    public DraftListResponseDto getDraftPosts(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        List<DraftListItemResponseDto> drafts = postRepository.findAll().stream()
                .filter(post -> post.getStatus() == PostStatus.DRAFT)
                .filter(post -> post.getAuthor() != null)
                .filter(post -> post.getAuthor().getId().equals(userId))
                .sorted(Comparator.comparing(Post::getModifiedAt).reversed())
                .map(DraftListItemResponseDto::new)
                .toList();

        return new DraftListResponseDto(drafts);
    }

    // @Transactional(readOnly = true)
    // 임시저장된 게시글 상세 조회
    public DraftResponseDto getDraftPost(Long userId, Long postId) {
        Post post = getDraftPostForAuthor(userId, postId);
        return new DraftResponseDto(post);
    }

    // @Transactional
    // 게시글 재임시저장
    public DraftResponseDto updateDraftPost(Long userId, Long postId, DraftRequestDto request) {
        Post post = getDraftPostForAuthor(userId, postId);

        post.updateDraft(request.getTitle(), request.getContent(), request.getImage());
        return new DraftResponseDto(post);
    }

    // @Transactional
    // 임시저장된 게시글 삭제
    public void deleteDraftPost(Long userId, Long postId) {
        getDraftPostForAuthor(userId, postId);
        postRepository.deleteById(postId);
    }

    private Post getDraftPostForAuthor(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        if (post.getAuthor() == null || !post.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN");
        }

        if (post.getStatus() != PostStatus.DRAFT) {
            throw new ForbiddenException("FORBIDDEN");
        }

        return post;
    }
}
