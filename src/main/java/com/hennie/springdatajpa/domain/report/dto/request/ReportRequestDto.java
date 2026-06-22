package com.hennie.springdatajpa.domain.report.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReportRequestDto {

    // 신고 사유 (필수)
    @NotBlank
    private String reason;
}