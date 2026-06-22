package com.hennie.springdatajpa.domain.post.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "post_image")
@Getter
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_image_id")
    private Long id;

    // N 이미지: 1 게시글 (양방향)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false) // FK
    private Post post;

    @Column(length = 1024, nullable = false)
    private String url;

    private int sortOrder;

    protected PostImage() {
    }

    public PostImage(Post post, String url, int sortOrder) {
        this.post = post;
        this.url = url;
        this.sortOrder = sortOrder;
    }
}
