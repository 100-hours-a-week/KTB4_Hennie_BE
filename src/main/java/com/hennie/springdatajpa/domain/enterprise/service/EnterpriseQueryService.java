package com.hennie.springdatajpa.domain.enterprise.service;

import com.hennie.springdatajpa.domain.enterprise.dto.response.EnterpriseResponseDto;
import com.hennie.springdatajpa.domain.enterprise.repository.EnterpriseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnterpriseQueryService {

    private final EnterpriseRepository enterpriseRepository;

    @Transactional(readOnly = true)
    public List<EnterpriseResponseDto> getEnterprises() {
        return enterpriseRepository.findAllByOrderByIdAsc().stream()
                .map(EnterpriseResponseDto::new)
                .toList();
    }
}
