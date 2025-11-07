package com.allra.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

/**
 * QueryDSL 설정 클래스
 * JPAQueryFactory 빈 등록
 * Repository에서 주입 받아 사용
 * 전역으로 하나의 JPAQueryFactory 인스턴스를 사용하도록 설정
 * 
 */
@Configuration
public class QuerydslConfig {

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
}
