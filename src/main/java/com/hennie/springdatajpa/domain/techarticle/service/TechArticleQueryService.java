package com.hennie.springdatajpa.domain.techarticle.service;

import com.hennie.springdatajpa.domain.techarticle.dto.response.TechArticleListItemResponseDto;
import com.hennie.springdatajpa.domain.techarticle.dto.response.TechArticleListResponseDto;
import com.hennie.springdatajpa.domain.techarticle.entity.TechArticle;
import com.hennie.springdatajpa.domain.techarticle.entity.TechArticleSource;
import com.hennie.springdatajpa.domain.techarticle.repository.TechArticleRepository;
import com.hennie.springdatajpa.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TechArticleQueryService {

    private final TechArticleRepository techArticleRepository;

    @Transactional(readOnly = true)
    public TechArticleListResponseDto getTechArticles(
            String enterprise,
            int page,
            int size
    ) {
        validatePageParameters(page, size);
        TechArticleSource selectedEnterprise = parseEnterprise(enterprise);
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<TechArticle> articlePage = selectedEnterprise == null
                ? techArticleRepository.findAll(pageable)
                : techArticleRepository.findByEnterprise(selectedEnterprise, pageable);
        List<TechArticleListItemResponseDto> articles = articlePage.getContent().stream()
                .map(TechArticleListItemResponseDto::new)
                .toList();

        return new TechArticleListResponseDto(
                articles,
                page,
                size,
                articlePage.getTotalElements(),
                articlePage.getTotalPages(),
                articlePage.hasNext()
        );
    }

    private void validatePageParameters(int page, int size) {
        if (page < 1 || size < 1) {
            throw new BadRequestException("invalidPageParameter");
        }
    }

    private TechArticleSource parseEnterprise(String enterprise) {
        if (enterprise == null || enterprise.isBlank()) {
            return null;
        }

        try {
            return TechArticleSource.valueOf(enterprise.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("invalidEnterprise");
        }
    }

}
