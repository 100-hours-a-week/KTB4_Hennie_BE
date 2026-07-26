package com.hennie.springdatajpa.domain.user.controller;

import com.hennie.springdatajpa.auth.config.RefreshCookieProperties;
import com.hennie.springdatajpa.auth.dto.LoginResultDto;
import com.hennie.springdatajpa.auth.dto.TokenInfo;
import com.hennie.springdatajpa.auth.dto.TokenResultDto;
import com.hennie.springdatajpa.auth.dto.request.LoginRequestDto;
import com.hennie.springdatajpa.auth.dto.response.LoginResponseDto;
import com.hennie.springdatajpa.domain.user.dto.request.PasswordChangeRequestDto;
import com.hennie.springdatajpa.domain.user.dto.request.UserInfoRequestDto;
import com.hennie.springdatajpa.domain.user.dto.request.UserRequestDto;
import com.hennie.springdatajpa.domain.user.dto.response.UserResponseDto;
import com.hennie.springdatajpa.domain.user.service.UserSignupService;
import com.hennie.springdatajpa.domain.user.service.UserService;
import com.hennie.springdatajpa.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final UserService userService;
    private final UserSignupService userSignupService;
    private final RefreshCookieProperties refreshCookieProperties;

    // 회원가입
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(
            @Valid @RequestPart("request") UserRequestDto request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        UserResponseDto result = userSignupService.signup(request, profileImage);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/users/" + result.getId())
                .body(ApiResponse.of("SIGNUP_SUCCESS", result));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto request,
            HttpServletResponse httpResponse
    ){
        LoginResultDto result = userService.login(request);

        ResponseCookie refreshCookie = createRefreshCookie(
                result.getRefreshToken(),
                refreshCookieProperties.getMaxAgeSeconds()
        );

        // 쿠키 응답 헤더에 추가
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("LOGIN_SUCCESS",result.getResponse()));
    }

    // 액세스 토큰 재발급
    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<TokenInfo>> refreshAccessToken(
            @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse httpResponse
    ){
        TokenResultDto result = userService.refreshAccessToken(refreshToken);

        // Refresh Token 회전 시 새 쿠키 세팅
        if (result.getNewRefreshToken() != null) {
            ResponseCookie cookie = createRefreshCookie(
                    result.getNewRefreshToken(),
                    refreshCookieProperties.getMaxAgeSeconds()
            );
            httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("TOKEN_REFRESH_SUCCESS", result.getToken()));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal Long userId,
            HttpServletResponse httpResponse
    ) {
        userService.logout(userId);

        ResponseCookie expiredCookie = createRefreshCookie("", 0);
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("LOGOUT_SUCCESS", null));
    }

    // 회원정보 조회
    @GetMapping("/myInfo")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUser(
            @AuthenticationPrincipal Long userId
    ) {
        UserResponseDto result = userService.getUser(userId);
        return ResponseEntity.ok(
                ApiResponse.of("GET_INFO_SUCCESS", result)
        );
    }

    // 회원정보 수정
    @PatchMapping("/myInfo")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateNickname(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserInfoRequestDto request
    ) {
        UserResponseDto result = userService.updateUser(userId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("UPDATE_INFO_SUCCESS", result));
    }

    // 비밀번호 수정
    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PasswordChangeRequestDto request
    ) {
        userService.changePassword(userId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("UPDATE_PASSWORD_SUCCESS", null));
    }

    // 회원 탈퇴
    @DeleteMapping("/myInfo")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal Long userId
    ) {
        userService.deleteUser(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("USER_DELETED", null));
    }

    private ResponseCookie createRefreshCookie(String token, long maxAgeSeconds) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(refreshCookieProperties.isSecure())
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite(refreshCookieProperties.getSameSite())
                .build();
    }
}
