package com.hennie.springdatajpa.domain.enterprise.dto.response;

import com.hennie.springdatajpa.domain.enterprise.entity.Enterprise;
import com.hennie.springdatajpa.domain.enterprise.entity.TechArticleCrawlSource;
import lombok.Getter;

@Getter
public class EnterpriseResponseDto {

    private final Long enterpriseId;
    private final TechArticleCrawlSource crawlSource;
    private final String name;
    private final boolean status;

    public EnterpriseResponseDto(Enterprise enterprise) {
        this.enterpriseId = enterprise.getId();
        this.crawlSource = enterprise.getCrawlSource();
        this.name = enterprise.getName();
        this.status = enterprise.isStatus();
    }
}
