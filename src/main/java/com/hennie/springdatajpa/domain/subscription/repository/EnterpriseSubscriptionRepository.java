package com.hennie.springdatajpa.domain.subscription.repository;

import com.hennie.springdatajpa.domain.subscription.dto.response.EnterpriseSubscriptionResponseDto;
import com.hennie.springdatajpa.domain.subscription.entity.EnterpriseSubscription;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnterpriseSubscriptionRepository
        extends JpaRepository<EnterpriseSubscription, Long> {

    Optional<EnterpriseSubscription> findByUserIdAndEnterpriseId(
            Long userId,
            Long enterpriseId
    );

    @Query("""
            select new com.hennie.springdatajpa.domain.subscription.dto.response.EnterpriseSubscriptionResponseDto(
                subscription.enterprise.id,
                subscription.status
            )
            from EnterpriseSubscription subscription
            where subscription.user.id = :userId
            order by subscription.createdAt desc
            """)
    List<EnterpriseSubscriptionResponseDto> findAllByUserIdOrderByCreatedAtDesc(
            @Param("userId") Long userId
    );

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("""
            select subscription
            from EnterpriseSubscription subscription
            join fetch subscription.user user
            where subscription.enterprise.id = :enterpriseId
              and subscription.enterprise.status = true
              and subscription.status = true
              and user.authorDeleted = false
            """)
    List<EnterpriseSubscription> findActiveRecipientsForNotification(
            @Param("enterpriseId") Long enterpriseId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update EnterpriseSubscription subscription
            set subscription.status = false
            where subscription.enterprise.id = :enterpriseId
              and subscription.status = true
            """)
    int disableAllByEnterpriseId(@Param("enterpriseId") Long enterpriseId);
}
