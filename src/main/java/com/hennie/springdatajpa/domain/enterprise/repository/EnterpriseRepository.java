package com.hennie.springdatajpa.domain.enterprise.repository;

import com.hennie.springdatajpa.domain.enterprise.entity.Enterprise;
import com.hennie.springdatajpa.domain.enterprise.entity.TechArticleCrawlSource;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {

    Optional<Enterprise> findByCrawlSource(TechArticleCrawlSource crawlSource);

    List<Enterprise> findAllByCrawlSourceIn(Collection<TechArticleCrawlSource> crawlSources);

    List<Enterprise> findAllByOrderByIdAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Enterprise e where e.id = :id")
    Optional<Enterprise> findByIdForUpdate(@Param("id") Long id);
}
