package com.hennie.springdatajpa.domain.enterprise.controller;

import com.hennie.springdatajpa.domain.enterprise.dto.response.EnterpriseResponseDto;
import com.hennie.springdatajpa.domain.enterprise.service.EnterpriseQueryService;
import com.hennie.springdatajpa.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/enterprises")
@RequiredArgsConstructor
public class EnterpriseController {

    private final EnterpriseQueryService enterpriseQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EnterpriseResponseDto>>> getEnterprises() {
        return ResponseEntity.ok(ApiResponse.of(
                "ENTERPRISES_FOUND",
                enterpriseQueryService.getEnterprises()
        ));
    }
}
