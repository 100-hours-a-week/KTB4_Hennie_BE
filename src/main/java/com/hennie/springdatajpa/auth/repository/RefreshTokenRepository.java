package com.hennie.springdatajpa.auth.repository;

import com.hennie.springdatajpa.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> { // 리프레스 토큰 엔티티에 대한 조회,삭제 담당
    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(Long userId);
}
