package com.hennie.springdatajpa.domain.post.entity;

// 게시글 유형. 썸네일 이미지는 유형에 따라 정해지므로 프론트가 이 값으로 asset을 고른다.
//   FE -> /assets/thumbnail_fe.png
//   BE -> /assets/thumbnail_be.png
//   AI -> /assets/thumbnail_ai.png
public enum PostCategory {
    FE,
    BE,
    AI
}
