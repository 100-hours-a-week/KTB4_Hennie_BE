package com.hennie.springdatajpa.domain.user.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Transactional
class UserTest {

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Rollback(false)
    void idTest() {
        User users = new User("tester@adapterz.kr", "123aS!", "Adapterz","imgUrl");
        entityManager.persist(users);
    }

    @Test
    @Rollback(false)
    void idStrategyTest(){
        // 5개의 더미데이터 추가
        for (int i = 1; i <= 5; i++) {
            User user = new User(
                    "tester" + i + "@adapterz.kr",
                    "123aS!" + i,
                    "Adapterz" + i,
                    "imgUrl" + i
            );
            entityManager.persist(user);
        }
    }
}