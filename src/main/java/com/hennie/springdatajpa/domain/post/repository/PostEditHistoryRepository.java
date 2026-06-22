package com.hennie.springdatajpa.domain.post.repository;

import com.hennie.springdatajpa.domain.post.entity.PostEditHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostEditHistoryRepository extends JpaRepository<PostEditHistory, Long> {
}