package com.hennie.springdatajpa.domain.user.service;

import com.hennie.springdatajpa.auth.dto.LoginResultDto;
import com.hennie.springdatajpa.auth.dto.TokenInfo;
import com.hennie.springdatajpa.auth.dto.TokenResultDto;
import com.hennie.springdatajpa.auth.dto.request.LoginRequestDto;
import com.hennie.springdatajpa.auth.dto.response.LoginResponseDto;
import com.hennie.springdatajpa.auth.entity.RefreshToken;
import com.hennie.springdatajpa.auth.jwt.JwtProvider;
import com.hennie.springdatajpa.auth.repository.RefreshTokenRepository;
import com.hennie.springdatajpa.domain.user.dto.request.PasswordChangeRequestDto;
import com.hennie.springdatajpa.domain.user.dto.request.UserInfoRequestDto;
import com.hennie.springdatajpa.domain.user.dto.request.UserRequestDto;
import com.hennie.springdatajpa.domain.user.dto.response.UserResponseDto;
import com.hennie.springdatajpa.domain.user.entity.User;
import com.hennie.springdatajpa.domain.user.repository.UserRepository;
import com.hennie.springdatajpa.global.exception.AuthorizedException;
import com.hennie.springdatajpa.global.exception.BadRequestException;
import com.hennie.springdatajpa.global.exception.DuplicateResourceException;
import com.hennie.springdatajpa.global.exception.NotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider; // JWT로 토큰 발급
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto createUser(@Valid UserRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("EMAIL_ALREADY_EXISTS");
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateResourceException("NICKNAME_ALREADY_EXISTS");
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname(),
                request.getProfileUrl()
        );
        User savedUser = userRepository.save(user);
        return new UserResponseDto(savedUser);
    }

    @Transactional
    public LoginResultDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthorizedException("INVALID_CREDENTIALS"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new AuthorizedException("INVALID_CREDENTIALS");
        }

        String accessToken = jwtProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );

        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        refreshTokenRepository.deleteByUserId(user.getId()); // 리프레시 토큰 새로 발급하면, 기존 리프레시 토큰 삭제
        refreshTokenRepository.save(
                new RefreshToken(
                        refreshToken,
                        user.getId(),
                        LocalDateTime.now().plusDays(14)
                )
        );

        return new LoginResultDto(
                LoginResponseDto.of(user, accessToken, jwtProvider.getAccessTokenValidityInMilliseconds()),
                refreshToken
        );
    }

    public TokenResultDto refreshAccessToken(String refreshToken) {
        RefreshToken saved = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AuthorizedException("UNAUTHORIZED"));

        if (saved.isExpired()) {
            refreshTokenRepository.delete(saved);
            throw new AuthorizedException("UNAUTHORIZED");
        }

        User user = userRepository.findById(saved.getUserId())
                .orElseThrow(() -> new AuthorizedException("UNAUTHORIZED"));

        String newAccessToken = jwtProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );

        // Refresh Token 회전 (Rotation)
        String newRefreshToken = jwtProvider.createRefreshToken(user.getId());
        refreshTokenRepository.delete(saved);
        refreshTokenRepository.save(
                new RefreshToken(
                        newRefreshToken,
                        user.getId(),
                        LocalDateTime.now().plusDays(14)
                )
        );

        return new TokenResultDto(
                new TokenInfo(newAccessToken, 3600),
                newRefreshToken
        );
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateUser(
            @Positive Long userId,
            @Valid UserInfoRequestDto request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        if (request.getNickname() != null) {
            if (!user.getNickname().equals(request.getNickname())
                    && userRepository.existsByNickname(request.getNickname())) {
                throw new DuplicateResourceException("NICKNAME_ALREADY_EXISTS");
            }
            user.changeNickname(request.getNickname());
        }

        if (request.getProfileUrl() != null) {
            user.changeProfileUrl(request.getProfileUrl());
        }

        return new UserResponseDto(user);
    }

    @Transactional
    public void changePassword(
            @Positive Long userId,
            @Valid PasswordChangeRequestDto request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AuthorizedException("INVALID_CREDENTIALS");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("SAME_AS_CURRENT_PASSWORD");
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));

        // 비밀번호 변경 시 기존 세션(리프레시 토큰) 무효화 → 다른 기기는 재로그인 필요
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        // 소프트 삭제: 레코드를 지우지 않고 authorDeleted 플래그만 세움.
        user.markAsDeleted();
    }
}
