package com.hennie.springdatajpa.domain.report.entity;

import com.hennie.springdatajpa.domain.post.entity.Post;
import com.hennie.springdatajpa.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "report",
        uniqueConstraints = {
                // 같은 신고자가 같은 게시글을 중복 신고하지 못하도록
                @UniqueConstraint(name = "uq_report_post", columnNames = {"reporter_id", "post_id"}),
                // 같은 신고자가 같은 사용자를 중복 신고하지 못하도록
                @UniqueConstraint(name = "uq_report_user", columnNames = {"reporter_id", "target_user_id"})
        }
)
@Getter
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReportType type;
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    // 신고한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false) // FK
    private User reporter;

    // 신고당한 사용자 (type == USER 일 때 사용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id") // FK
    private User targetUser;

    // 신고당한 게시글 (type == POST 일 때 사용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id") // FK
    private Post post;

    private String reason;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Report() {
    }

    private Report(ReportType type, User reporter, User targetUser, Post post, String reason) {
        this.type = type;
        this.status = ReportStatus.PENDING;
        this.reporter = reporter;
        this.targetUser = targetUser;
        this.post = post;
        this.reason = reason;
    }

    public static Report forPost(User reporter, Post post, String reason) {
        return new Report(ReportType.POST, reporter, null, post, reason);
    }
}
