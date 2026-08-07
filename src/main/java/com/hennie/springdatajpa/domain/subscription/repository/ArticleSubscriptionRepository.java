package com.hennie.springdatajpa.domain.subscription.repository;

import com.hennie.springdatajpa.domain.subscription.entity.ArticleSubscription;
import com.hennie.springdatajpa.domain.subscription.dto.response.ArticleSubscriptionResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticleSubscriptionRepository extends JpaRepository<ArticleSubscription, Long> {

    Optional<ArticleSubscription> findByUserIdAndEnterpriseId(Long userId, Long enterpriseId);

    @Query("""
            select new com.hennie.springdatajpa.domain.subscription.dto.response.ArticleSubscriptionResponseDto(
                subscription.enterprise.id,
                subscription.status
            )
            from ArticleSubscription subscription
            where subscription.user.id = :userId
            order by subscription.createdAt desc
            """)
    List<ArticleSubscriptionResponseDto> findAllByUserIdOrderByCreatedAtDesc(
            @Param("userId") Long userId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update ArticleSubscription subscription
            set subscription.status = false
            where subscription.enterprise.id = :enterpriseId
              and subscription.status = true
            """)
    int disableAllByEnterpriseId(@Param("enterpriseId") Long enterpriseId);
}
