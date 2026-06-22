package com.hennie.springdatajpa.domain.user.dto.response;

import lombok.Getter;
import com.hennie.springdatajpa.domain.user.entity.User;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String profileUrl;
    private boolean authorDeleted;
    private String createdAt;
    private String modifiedAt;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileUrl = user.getProfileUrl();
        this.authorDeleted = user.isAuthorDeleted();
        this.createdAt = user.getFormattedCreatedAt();
        this.modifiedAt = user.getFormattedModifiedAt();
    }
}