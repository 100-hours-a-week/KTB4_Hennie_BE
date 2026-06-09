package com.hennie.springdatajpa.domain.post.repository;

import com.hennie.springdatajpa.domain.post.entity.Post;
import com.hennie.springdatajpa.global.store.InMemoryDataStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InMemoryPostRepository implements PostRepository {

    private final InMemoryDataStore store;

    @Override
    public Post save(Post post) {
        if (post.getId() == null) {
            post.assignId(store.nextPostId());
        }
        store.getPosts().put(post.getId(), post);
        return post;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(store.getPosts().get(id));
    }

    @Override
    public List<Post> findAll() {
        return new ArrayList<>(store.getPosts().values());
    }

    // 특정 게시글 삭제
    @Override
    public void deleteById(Long id) {
        store.getPosts().remove(id);
    }

    // 사용자 기준으로 해당 사용자가 쓴 게시글 전체 삭제 (탈퇴했을 때)
    @Override
    public void deleteByAuthorId(Long authorId) {
        store.getPosts().entrySet()
                .removeIf(entry -> entry.getValue().getAuthor() != null
                        && entry.getValue().getAuthor().getId().equals(authorId));
    }

    @Override
    public void anonymizeAuthorById(Long authorId) {
        store.getPosts().values().stream()
                .filter(post -> post.getAuthor() != null)
                .filter(post -> post.getAuthor().getId().equals(authorId))
                .forEach(Post::anonymizeAuthor);
    }
}
