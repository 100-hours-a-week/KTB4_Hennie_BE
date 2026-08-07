package com.hennie.springdatajpa.domain.techarticle.repository;

import com.hennie.springdatajpa.domain.techarticle.entity.TechArticle;
import com.hennie.springdatajpa.domain.enterprise.entity.TechArticleCrawlSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TechArticleRepository extends JpaRepository<TechArticle, Long> {
    List<TechArticle> findAllByOriginalUrlIn(Collection<String> originalUrls);

    @Query("""
            select article
            from TechArticle article
            join fetch article.enterprise
            where article.id = :articleId
            """)
    Optional<TechArticle> findByIdWithEnterprise(@Param("articleId") Long articleId);

    @Override
    @EntityGraph(attributePaths = "enterprise")
    Page<TechArticle> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "enterprise")
    Page<TechArticle> findByEnterpriseCrawlSource(
            TechArticleCrawlSource crawlSource,
            Pageable pageable
    );
}
