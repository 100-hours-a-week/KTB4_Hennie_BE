package com.hennie.springdatajpa.domain.like.service;

import com.hennie.springdatajpa.domain.like.dto.response.PostLikeResponseDto;
import com.hennie.springdatajpa.domain.like.entity.PostLike;
import com.hennie.springdatajpa.domain.like.repository.PostLikeRepository;
import com.hennie.springdatajpa.domain.post.entity.Post;
import com.hennie.springdatajpa.domain.post.entity.PostStatus;
import com.hennie.springdatajpa.domain.post.repository.PostRepository;
import com.hennie.springdatajpa.domain.user.entity.User;
import com.hennie.springdatajpa.domain.user.repository.UserRepository;
import com.hennie.springdatajpa.global.exception.DuplicateResourceException;
import com.hennie.springdatajpa.global.exception.ForbiddenException;
import com.hennie.springdatajpa.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostLikeResponseDto likePost(Long userId, Long postId) {
        User user = validateUser(userId);
        Post post = validateLikablePost(postId);

        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new DuplicateResourceException("ALREADY_LIKED_POST");
        }

        postLikeRepository.save(new PostLike(post, user));
        return new PostLikeResponseDto(postId, getLikeCount(postId), true);
    }

    @Transactional
    public PostLikeResponseDto unlikePost(Long userId, Long postId) {
        validateUser(userId);
        validateLikablePost(postId);

        PostLike postLike = postLikeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new NotFoundException("POST_LIKE_NOT_FOUND"));

        postLikeRepository.delete(postLike);
        return new PostLikeResponseDto(postId, getLikeCount(postId), false);
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
    }

    private Post validateLikablePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        if (post.getStatus() != PostStatus.PUBLISHED || post.isBlinded()) {
            throw new ForbiddenException("FORBIDDEN");
        }
        return post;
    }

    private int getLikeCount(Long postId) {
        return Math.toIntExact(postLikeRepository.countByPostId(postId));
    }
}
