package com.hennie.springdatajpa.domain.comment.service;

import com.hennie.springdatajpa.domain.comment.dto.request.CommentRequestDto;
import com.hennie.springdatajpa.domain.comment.dto.request.ReplyCreateRequestDto;
import com.hennie.springdatajpa.domain.comment.dto.response.CommentResponseDto;
import com.hennie.springdatajpa.domain.comment.dto.response.ReplyResponseDto;
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

    @Transactional
    public ReplyResponseDto createReply(
            Long userId,
            Long postId,
            Long commentId,
            ReplyCreateRequestDto request
    ) {
        Post post = validateCommentablePost(postId);
        Comment parentComment = getComment(postId, commentId);

        if (parentComment.isDeleted()) {
            throw new BadRequestException("DELETED_COMMENT");
        }

        Comment replyTo = getReplyTarget(postId, parentComment, request.getReplyToId());
        validateReplyTarget(parentComment, replyTo);

        User author = getUser(userId);
        Comment reply = new Comment(
                post,
                author,
                request.getContent(),
                parentComment,
                replyTo
        );
        Comment savedReply = commentRepository.save(reply);
        return new ReplyResponseDto(savedReply);
    }

    @Transactional
    public ReplyResponseDto updateReply(
            Long userId,
            Long postId,
            Long commentId,
            Long replyId,
            CommentRequestDto request
    ) {
        validateCommentablePost(postId);
        Comment reply = getReply(postId, commentId, replyId);
        validateAuthor(reply, userId);

        if (reply.isDeleted()) {
            throw new BadRequestException("DELETED_REPLY");
        }

        if (Objects.equals(reply.getContent(), request.getContent())) {
            throw new BadRequestException("noChangedValue");
        }

        reply.update(request.getContent());
        return new ReplyResponseDto(reply);
    }

    @Transactional
    public void deleteReply(
            Long userId,
            Long postId,
            Long commentId,
            Long replyId
    ) {
        validateCommentablePost(postId);
        Comment reply = getReply(postId, commentId, replyId);
        validateAuthor(reply, userId);

        if (reply.isDeleted()) {
            throw new BadRequestException("DELETED_REPLY");
        }

        reply.delete();
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
        return commentRepository.findByIdAndPostIdAndParentIsNull(commentId, postId)
                .orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND"));
    }

    private Comment getReply(Long postId, Long commentId, Long replyId) {
        return commentRepository.findByIdAndPostIdAndParentId(replyId, postId, commentId)
                .orElseThrow(() -> new NotFoundException("REPLY_NOT_FOUND"));
    }

    private Comment getReplyTarget(Long postId, Comment parentComment, Long replyToId) {
        if (replyToId == null || Objects.equals(replyToId, parentComment.getId())) {
            return parentComment;
        }

        return commentRepository.findByIdAndPostId(replyToId, postId)
                .orElseThrow(() -> new NotFoundException("REPLY_TARGET_NOT_FOUND"));
    }

    private void validateReplyTarget(Comment parentComment, Comment replyTo) {
        Comment targetParent = replyTo.getParent();
        Long targetRootId = targetParent == null
                ? replyTo.getId()
                : targetParent.getId();

        if (!Objects.equals(parentComment.getId(), targetRootId)) {
            throw new BadRequestException("INVALID_REPLY_TARGET");
        }

        if (replyTo.isDeleted()) {
            throw new BadRequestException("DELETED_REPLY_TARGET");
        }
    }

    private void validateAuthor(Comment comment, Long userId) {
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN");
        }
    }
}
