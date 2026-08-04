package com.hennie.springdatajpa.domain.report.dto.request;

import com.hennie.springdatajpa.domain.report.entity.ReportReason;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequestDto {

    @NotNull(message = "신고 사유는 필수값입니다.")
    private ReportReason reason;
}