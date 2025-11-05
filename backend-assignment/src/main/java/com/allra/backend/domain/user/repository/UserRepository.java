package com.allra.backend.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.allra.backend.domain.user.entity.UserEntity;

/**
 * 사용자 기본 리포지토리
 * JPA 기본 메서드 사용
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
