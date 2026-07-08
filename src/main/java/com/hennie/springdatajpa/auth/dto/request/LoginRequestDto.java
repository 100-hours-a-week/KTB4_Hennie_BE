package com.hennie.springdatajpa.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto { // 로그인 요청 시 필요한 입력값에 대한 검증 규칙 정의
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수값입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수값입니다.")
    @Size(min = 8, message = "TOO_SHORT") // 길이가 8 미만
    @Size(max = 20, message = "TOO_LONG") // 길이가 20 초과
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "INVALID_FORMAT"
    ) // 영문, 숫자, 특수문자를 최소 1개씩 포함해야 함
    private String password;
}
