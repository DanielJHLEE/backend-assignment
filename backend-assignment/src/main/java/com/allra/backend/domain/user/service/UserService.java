package com.allra.backend.domain.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.allra.backend.domain.user.dto.UserDto;
import com.allra.backend.domain.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 전체 사용자 조회
    public List<UserDto.Response> findAll() {
        return userRepository.findAll().stream()
                .map(user -> UserDto.Response.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .createdAt(user.getCreatedAt())
                        .build())
                .toList();
    }

    // ID로 사용자 조회
    public Optional<UserDto.Response> findById(Long id) {
        return userRepository.findById(id)
                .map(user -> UserDto.Response.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .createdAt(user.getCreatedAt())
                        .build());
    }
}
