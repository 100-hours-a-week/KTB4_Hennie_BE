package com.hennie.springdatajpa.domain.user.entity;

import com.hennie.springdatajpa.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_user_email", columnNames = "email"),
                @UniqueConstraint(name = "uq_user_nickname", columnNames = "nickname")
        }
)
@Getter
public class User {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    private String profileUrl;

    @Column(nullable = false)
    private boolean authorDeleted;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    // 1 사용자: N 게시글 (양방향)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    private List<Post> posts = new ArrayList<>();

    public User() {

    }

    public User(String email, String password, String nickname, String profileUrl) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // soft delete: 탈퇴 표시
    public void markAsDeleted() {
        if (this.authorDeleted) {
            return;
        }
        this.authorDeleted = true;
        this.email = this.email + "#del#" + this.id;
        this.nickname = this.nickname + "#del#" + this.id;
    }

    // 회원정보 생성하기
    public String getFormattedCreatedAt() {
        return createdAt.format(DATE_TIME_FORMATTER);
    }

    // 회원정보 수정하기
    public String getFormattedModifiedAt() {
        return modifiedAt.format(DATE_TIME_FORMATTER);
    }
}
