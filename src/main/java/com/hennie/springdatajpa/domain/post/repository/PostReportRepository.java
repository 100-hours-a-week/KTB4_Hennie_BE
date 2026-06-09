package com.hennie.springdatajpa.domain.post.repository;

import com.hennie.springdatajpa.domain.post.entity.PostReport;

public interface PostReportRepository {

    PostReport save(PostReport postReport);

    boolean existsByPostIdAndReporterId(Long postId, Long reporterId);
}
