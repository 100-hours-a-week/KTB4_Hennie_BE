package com.hennie.springdatajpa.domain.post.repository;

import com.hennie.springdatajpa.domain.post.entity.PostReport;
import com.hennie.springdatajpa.global.store.InMemoryDataStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InMemoryPostReportRepository implements PostReportRepository {

    private final InMemoryDataStore store;

    @Override
    public PostReport save(PostReport postReport) {
        if (postReport.getId() == null) {
            postReport.assignId(store.nextPostReportId());
        }

        store.getPostReports().put(postReport.getId(), postReport);
        return postReport;
    }

    @Override
    public boolean existsByPostIdAndReporterId(Long postId, Long reporterId) {
        return store.getPostReports().values().stream()
                .anyMatch(report -> report.getPostId().equals(postId)
                        && report.getReporterId().equals(reporterId));
    }
}
