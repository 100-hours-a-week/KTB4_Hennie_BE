package com.hennie.springdatajpa.domain.user.service;

import com.hennie.springdatajpa.auth.dto.LoginResultDto;
import com.hennie.springdatajpa.auth.dto.request.LoginRequestDto;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private JwtProvider jwtProvider;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    private LoginRequestDto loginRequestDto(String email, String password) {
        LoginRequestDto dto = new LoginRequestDto();
        ReflectionTestUtils.setField(dto, "email", email);
        ReflectionTestUtils.setField(dto, "password", password);
        return dto;
    }

    private UserRequestDto signupRequestDto(String email, String password, String nickname, String profileUrl) {
        UserRequestDto dto = new UserRequestDto();
        ReflectionTestUtils.setField(dto, "email", email);
        ReflectionTestUtils.setField(dto, "password", password);
        ReflectionTestUtils.setField(dto, "nickname", nickname);
        ReflectionTestUtils.setField(dto, "profileUrl", profileUrl);
        return dto;
    }

    private UserInfoRequestDto userInfoRequestDto(String nickname, String profileUrl) {
        UserInfoRequestDto dto = new UserInfoRequestDto();
        ReflectionTestUtils.setField(dto, "nickname", nickname);
        ReflectionTestUtils.setField(dto, "profileUrl", profileUrl);
        return dto;
    }

    private PasswordChangeRequestDto passwordChangeRequestDto(String currentPassword, String newPassword) {
        PasswordChangeRequestDto dto = new PasswordChangeRequestDto();
        ReflectionTestUtils.setField(dto, "currentPassword", currentPassword);
        ReflectionTestUtils.setField(dto, "newPassword", newPassword);
        return dto;
    }

    private User persistedUser(Long id, String email, String encodedPassword, String nickname) {
        User user = new User(email, encodedPassword, nickname, null);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(user, "modifiedAt", LocalDateTime.now());
        return user;
    }

    @Nested
    class CreateUser {

        @Test
        void 회원가입_성공() {
            // given
            UserRequestDto request = signupRequestDto("tester1@adapterz.kr", "Raw1!", "nick", "img");
            given(userRepository.existsByEmail("tester1@adapterz.kr")).willReturn(false);
            given(userRepository.existsByNickname("nick")).willReturn(false);
            given(passwordEncoder.encode("Raw1!")).willReturn("ENCODED_PWD");
            given(userRepository.save(any(User.class)))
                    .willReturn(persistedUser(1L, "tester1@adapterz.kr", "ENCODED_PWD", "nick"));

            // when
            UserResponseDto result = userService.createUser(request);

            // then
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEmail()).isEqualTo("tester1@adapterz.kr");
            assertThat(result.getNickname()).isEqualTo("nick");

            verify(userRepository).save(any(User.class));
        }

        @Test
        void 이메일_중복() {
            // given
            UserRequestDto request = signupRequestDto("tester1@adapterz.kr", "Raw1!", "nick", null);
            given(userRepository.existsByEmail("tester1@adapterz.kr")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.createUser(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("EMAIL_ALREADY_EXISTS");
            verify(userRepository, never()).save(any());
        }

        @Test
        void 닉네임_중복() {
            // given
            UserRequestDto request = signupRequestDto("tester1@adapterz.kr", "Raw1!", "dupNick", null);
            given(userRepository.existsByEmail("tester1@adapterz.kr")).willReturn(false);
            given(userRepository.existsByNickname("dupNick")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.createUser(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("NICKNAME_ALREADY_EXISTS");
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class Login {

        @Test
        void 정상_로그인_성공() {
            // given
            LoginRequestDto request = loginRequestDto("tester1@adapterz.kr", "Raw1!");
            User user = persistedUser(1L, "tester1@adapterz.kr", "ENCODED_PWD", "nick");

            given(userRepository.findByEmail("tester1@adapterz.kr")).willReturn(Optional.of(user));
            given(passwordEncoder.matches("Raw1!", "ENCODED_PWD")).willReturn(true);
            given(jwtProvider.createAccessToken(1L, "tester1@adapterz.kr", "nick"))
                    .willReturn("ACCESS_TOKEN");
            given(jwtProvider.createRefreshToken(1L)).willReturn("REFRESH_TOKEN");
            given(jwtProvider.getAccessTokenValidityInMilliseconds()).willReturn(300000L);

            // when
            LoginResultDto result = userService.login(request);

            // then
            assertThat(result.getResponse().getUser()).isEqualTo(user);
            assertThat(result.getResponse().getToken().getAccessToken()).isEqualTo("ACCESS_TOKEN");
            assertThat(result.getResponse().getToken().getExpiresIn()).isEqualTo(300000L);
            assertThat(result.getRefreshToken()).isEqualTo("REFRESH_TOKEN");

            verify(refreshTokenRepository).deleteByUserId(1L);

            ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
            verify(refreshTokenRepository).save(captor.capture());
            assertThat(captor.getValue().getToken()).isEqualTo("REFRESH_TOKEN");
            assertThat(captor.getValue().getUserId()).isEqualTo(1L);
            assertThat(captor.getValue().getExpiresAt()).isAfter(LocalDateTime.now());
        }

        @Test
        @DisplayName("이메일 불일치")
        void 등록되지_않은_사용자() {
            // given
            LoginRequestDto request = loginRequestDto("unknown@adapterz.kr", "Raw1!");
            given(userRepository.findByEmail("unknown@adapterz.kr")).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.login(request))
                    .isInstanceOf(AuthorizedException.class)
                    .hasMessage("INVALID_CREDENTIALS");

            verify(passwordEncoder, never()).matches(any(), any());
            verify(jwtProvider, never()).createAccessToken(any(), any(), any());
            verify(refreshTokenRepository, never()).save(any());
        }

        @Test
        void 비밀번호_불일치() {
            // given
            LoginRequestDto request = loginRequestDto("tester1@adapterz.kr", "Wrong1!");
            User user = persistedUser(1L, "tester1@adapterz.kr", "ENCODED_PWD", "nick");

            given(userRepository.findByEmail("tester1@adapterz.kr")).willReturn(Optional.of(user));
            given(passwordEncoder.matches("Wrong1!", "ENCODED_PWD")).willReturn(false);

            // when & then
            assertThatThrownBy(() -> userService.login(request))
                    .isInstanceOf(AuthorizedException.class)
                    .hasMessage("INVALID_CREDENTIALS");

            verify(jwtProvider, never()).createAccessToken(any(), any(), any());
            verify(jwtProvider, never()).createRefreshToken(any());
            verify(refreshTokenRepository, never()).save(any());
        }
    }

    @Nested
    class Logout {

        @Test
        void 로그아웃_성공(){
            // when
            userService.logout(1L);

            // then
            verify(refreshTokenRepository).deleteByUserId(1L);
        }

        @Test
        @DisplayName("로그아웃을 두 번 호출해도 예외 없이 삭제 요청 두 번 수행. 회원인지 확인 X - DB 접근 최소화")
        void 재로그아웃_멱등(){
            // when
            userService.logout(1L);
            userService.logout(1L);

            // then
            verify(refreshTokenRepository, times(2)).deleteByUserId(1L);
        }
    }

    @Nested
    class GetUser {

        @Test
        void 회원조회_성공() {
            // given
            User user = persistedUser(1L, "tester1@adapterz.kr", "ENCODED_PWD", "nick");
            given(userRepository.findById(1L)).willReturn(Optional.of(user));

            // when
            UserResponseDto result = userService.getUser(1L);

            // then
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEmail()).isEqualTo("tester1@adapterz.kr");
            assertThat(result.getNickname()).isEqualTo("nick");
        }

        @Test
        void 등록되지_않은_사용자() {
            // given
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUser(999L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("USER_NOT_FOUND");
        }
    }

    @Nested
    class UpdateUser {

        @Test
        void 회원정보_수정_정상(){
            // given
            User user = persistedUser(1L, "tester1@adapterz.kr", "ENCODED_PWD", "oldNick");
            UserInfoRequestDto request = userInfoRequestDto("newNick", "newImgUrl");

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(userRepository.existsByNickname("newNick")).willReturn(false);

            // when
            UserResponseDto result = userService.updateUser(1L, request);

            // then
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getNickname()).isEqualTo("newNick");
            assertThat(result.getProfileUrl()).isEqualTo("newImgUrl");
            assertThat(user.getNickname()).isEqualTo("newNick");
            assertThat(user.getProfileUrl()).isEqualTo("newImgUrl");
        }

        @Test
        void 등록되지_않은_사용자(){
            // given
            UserInfoRequestDto request = userInfoRequestDto("newNick", "newImgUrl");
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.updateUser(999L, request))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("USER_NOT_FOUND");

            verify(userRepository, never()).existsByNickname(any());
        }

        @Test
        void 잘못된_입력_형식(){
            // UserInfoRequestDto의 @Pattern, @Size 검증은 Bean Validation 영역이다.
            // 현재 Mockito 기반 UserService 단위 테스트에서는 Spring Validation 프록시가 실행되지 않는다.
            // 이 케이스는 Controller 테스트 또는 Validator 테스트에서 검증한다.
        }

        @Test
        void 닉네임_중복(){
            // given
            User user = persistedUser(1L, "tester1@adapterz.kr", "ENCODED_PWD", "oldNick");
            UserInfoRequestDto request = userInfoRequestDto("dupNick", null);

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(userRepository.existsByNickname("dupNick")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.updateUser(1L, request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("NICKNAME_ALREADY_EXISTS");

            assertThat(user.getNickname()).isEqualTo("oldNick");
            verify(userRepository).existsByNickname("dupNick");
        }

    }

    @Nested
    class ChangePassword {

        @Test
        void 비밀번호_변경_성공() {
            // given
            User user = persistedUser(1L, "tester1@adapterz.kr", "ENCODED_PWD", "nick");
            PasswordChangeRequestDto request = passwordChangeRequestDto("Current1!", "New12345!");

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(passwordEncoder.matches("Current1!", "ENCODED_PWD")).willReturn(true);
            given(passwordEncoder.matches("New12345!", "ENCODED_PWD")).willReturn(false);
            given(passwordEncoder.encode("New12345!")).willReturn("NEW_ENCODED_PWD");

            // when
            userService.changePassword(1L, request);

            // then
            assertThat(user.getPassword()).isEqualTo("NEW_ENCODED_PWD");
            verify(refreshTokenRepository).deleteByUserId(1L);
        }

        @Test
        void 등록되지_않은_사용자() {
            // given
            PasswordChangeRequestDto request = passwordChangeRequestDto("Current1!", "New12345!");
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.changePassword(999L, request))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("USER_NOT_FOUND");

            verify(passwordEncoder, never()).matches(any(), any());
            verify(refreshTokenRepository, never()).deleteByUserId(any());
        }

        @Test
        void 현재_비밀번호_불일치() {
            // given
            User user = persistedUser(1L, "tester1@adapterz.kr", "ENCODED_PWD", "nick");
            PasswordChangeRequestDto request = passwordChangeRequestDto("Wrong1!", "New12345!");

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(passwordEncoder.matches("Wrong1!", "ENCODED_PWD")).willReturn(false);

            // when & then
            assertThatThrownBy(() -> userService.changePassword(1L, request))
                    .isInstanceOf(AuthorizedException.class)
                    .hasMessage("INVALID_CREDENTIALS");

            assertThat(user.getPassword()).isEqualTo("ENCODED_PWD");
            verify(passwordEncoder, never()).encode(any());
            verify(refreshTokenRepository, never()).deleteByUserId(any());
        }

        @Test
        void 새_비밀번호가_현재_비밀번호와_같음() {
            // given
            User user = persistedUser(1L, "tester1@adapterz.kr", "ENCODED_PWD", "nick");
            PasswordChangeRequestDto request = passwordChangeRequestDto("Current1!", "Current1!");

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(passwordEncoder.matches("Current1!", "ENCODED_PWD")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.changePassword(1L, request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("SAME_AS_CURRENT_PASSWORD");

            assertThat(user.getPassword()).isEqualTo("ENCODED_PWD");
            verify(passwordEncoder, never()).encode(any());
            verify(refreshTokenRepository, never()).deleteByUserId(any());
        }
    }

    @Nested
    class DeleteUser {

        @Test
        void 회원_탈퇴_성공() {
            // given
            User user = persistedUser(1L, "tester1@adapterz.kr", "ENCODED_PWD", "nick");
            given(userRepository.findById(1L)).willReturn(Optional.of(user));

            // when
            userService.deleteUser(1L);

            // then
            assertThat(user.isAuthorDeleted()).isTrue();
            assertThat(user.getEmail()).isEqualTo("tester1@adapterz.kr#del#1");
            assertThat(user.getNickname()).isEqualTo("nick#del#1");
        }

        @Test
        void 존재하지않는_사용자() {
            // given
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.deleteUser(999L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("USER_NOT_FOUND");
        }

        @Test
        @DisplayName("markAsDeleted의 멱등성을 검증한다.")
        void 회원_재탈퇴() {
            // given
            User user = persistedUser(1L, "tester1@adapterz.kr", "ENCODED_PWD", "nick");
            given(userRepository.findById(1L)).willReturn(Optional.of(user));

            // when
            userService.deleteUser(1L);
            String deletedEmail = user.getEmail();
            String deletedNickname = user.getNickname();

            userService.deleteUser(1L);

            // then
            assertThat(user.isAuthorDeleted()).isTrue();
            assertThat(user.getEmail()).isEqualTo(deletedEmail);
            assertThat(user.getNickname()).isEqualTo(deletedNickname);
        }
    }

}
