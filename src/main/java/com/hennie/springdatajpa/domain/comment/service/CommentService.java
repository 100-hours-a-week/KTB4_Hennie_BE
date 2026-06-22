package com.hennie.springdatajpa.domain.comment.service;

import com.hennie.springdatajpa.domain.comment.dto.request.CommentRequestDto;
import com.hennie.springdatajpa.domain.comment.dto.response.CommentResponseDto;
import com.hennie.springdatajpa.domain.comment.entity.Comment;
import com.hennie.springdatajpa.domain.comment.repository.CommentRepository;
import com.hennie.springdatajpa.domain.post.entity.Post;
import com.hennie.springdatajpa.domain.post.entity.PostStatus;
import com.hennie.springdatajpa.domain.post.repository.PostRepository;
import com.hennie.springdatajpa.domain.user.entity.User;
import com.hennie.springdatajpa.domain.user.repository.UserRepository;
import com.hennie.springdatajpa.global.exception.BadRequestException;
import com.hennie.springdatajpa.global.exception.ForbiddenException;
import com.hennie.springdatajpa.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponseDto createComment(Long userId, Long postId, CommentRequestDto request) {
        Post post = validateCommentablePost(postId);
        User author = getUser(userId);

        Comment comment = new Comment(post, author, request.getContent());
        Comment savedComment = commentRepository.save(comment);

        return new CommentResponseDto(savedComment);
    }

    @Transactional
    public CommentResponseDto updateComment(Long userId, Long postId, Long commentId, CommentRequestDto request) {
        validateCommentablePost(postId);
        Comment comment = getComment(postId, commentId);
        validateAuthor(comment, userId);

        if (comment.isDeleted()) {
            throw new BadRequestException("DELETED_COMMENT");
        }

        if (Objects.equals(comment.getContent(), request.getContent())) {
            throw new BadRequestException("noChangedValue");
        }

        comment.update(request.getContent());
        return new CommentResponseDto(comment);
    }

    @Transactional
    public void deleteComment(Long userId, Long postId, Long commentId) {
        validateCommentablePost(postId);
        Comment comment = getComment(postId, commentId);
        validateAuthor(comment, userId);

        if (comment.isDeleted()) {
            throw new BadRequestException("DELETED_COMMENT");
        }

        comment.delete();
    }

    // 게시글 존재 확인
    private Post validateCommentablePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        if (post.getStatus() != PostStatus.PUBLISHED || post.isBlinded()) {
            throw new ForbiddenException("FORBIDDEN");
        }
        return post;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
    }

    private Comment getComment(Long postId, Long commentId) {
        return commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND"));
    }

    private void validateAuthor(Comment comment, Long userId) {
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN");
        }
    }
}
