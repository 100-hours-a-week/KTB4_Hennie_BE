package com.hennie.springdatajpa.domain.techarticle.repository;

import com.hennie.springdatajpa.domain.techarticle.entity.TechArticle;
import com.hennie.springdatajpa.domain.enterprise.entity.TechArticleCrawlSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TechArticleRepository extends JpaRepository<TechArticle, Long> {
    List<TechArticle> findAllByOriginalUrlIn(Collection<String> originalUrls);

    @Override
    @EntityGraph(attributePaths = "enterprise")
    Page<TechArticle> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "enterprise")
    Page<TechArticle> findByEnterpriseCrawlSource(
            TechArticleCrawlSource crawlSource,
            Pageable pageable
    );
}
