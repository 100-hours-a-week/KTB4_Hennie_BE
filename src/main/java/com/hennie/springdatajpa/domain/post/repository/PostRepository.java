package com.hennie.springdatajpa.domain.post.repository;

import com.hennie.springdatajpa.domain.post.entity.Post;

import java.util.List;
import java.util.Optional;

// DB 사용 시:
// public interface PostRepository extends JpaRepository<Post, Long> {
// }

// DB 없이 Map 기반으로 구현
public interface PostRepository {
    Post save(Post post);

    Optional<Post> findById(Long id);

    List<Post> findAll();

    void deleteById(Long id);

    void deleteByAuthorId(Long authorId);

    void anonymizeAuthorById(Long authorId);
}
