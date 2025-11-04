package com.allra.backend.domain.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.allra.backend.domain.user.service.UserService;

import com.allra.backend.domain.user.dto.UserDto;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 전체 사용자 조회
    @GetMapping
    public List<UserDto.UserResponseDto> getAllUsers() {
        return userService.findAll(); // DTO로 이미 변환된 리스트 반환
    }

    // ID로 사용자 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserDto.UserResponseDto> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
