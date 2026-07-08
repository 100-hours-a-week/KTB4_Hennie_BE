package com.hennie.springdatajpa.domain.user.controller;

import com.hennie.springdatajpa.auth.dto.LoginResultDto;
import com.hennie.springdatajpa.auth.dto.TokenInfo;
import com.hennie.springdatajpa.auth.dto.TokenResultDto;
import com.hennie.springdatajpa.auth.dto.request.LoginRequestDto;
import com.hennie.springdatajpa.auth.dto.response.LoginResponseDto;
import com.hennie.springdatajpa.domain.user.dto.request.PasswordChangeRequestDto;
import com.hennie.springdatajpa.domain.user.dto.request.UserInfoRequestDto;
import com.hennie.springdatajpa.domain.user.dto.request.UserRequestDto;
import com.hennie.springdatajpa.domain.user.dto.response.UserResponseDto;
import com.hennie.springdatajpa.domain.user.service.UserService;
import com.hennie.springdatajpa.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(@Valid @RequestBody UserRequestDto request) {
        UserResponseDto result = userService.createUser(request);
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

        ResponseCookie refreshCookie = ResponseCookie
                .from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(14*24*60*60)
                .sameSite("Strict")
                .build();

        // 쿠키 응답 헤더에 추가
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("LOGIN_SUCCESS",result.getResponse()));
    }

    // 액세스 토큰 재발급
    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<TokenInfo>> refreshAccessToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse httpResponse
    ){
        TokenResultDto result = userService.refreshAccessToken(refreshToken);

        // Refresh Token 회전 시 새 쿠키 세팅
        if (result.getNewRefreshToken() != null) {
            ResponseCookie cookie = ResponseCookie.from("refreshToken", result.getNewRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(14*24*60*60)
                    .sameSite("Lax")
                    .build();
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

        ResponseCookie expiredCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
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
}
