package com.hennie.springdatajpa.domain.report.repository;

import com.hennie.springdatajpa.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // 같은 사용자가 같은 게시글을 중복 신고했는지 확인
    boolean existsByPostIdAndReporterId(Long postId, Long reporterId);

    // 게시글 삭제 시 그 글에 대한 신고(type=POST) 정리
    void deleteByPostId(Long postId);
}