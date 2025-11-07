package com.allra.backend.domain.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.allra.backend.domain.user.dto.UserDto;
import com.allra.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * User Service
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 전체 사용자 조회
    public List<UserDto.UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserDto.UserResponseDto::fromEntity) // 변환 메서드 사용
                .toList();
    }

    // ID로 사용자 조회
    public Optional<UserDto.UserResponseDto> findById(Long id) {
        return userRepository.findById(id)
                .map(UserDto.UserResponseDto::fromEntity);
    }
}
