package com.allra.backend.domain.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.allra.backend.domain.user.dto.UserDto;
import com.allra.backend.domain.user.service.UserService;
import com.allra.backend.global.dto.ApiResponseDto;

import lombok.RequiredArgsConstructor;

/**
 * User Controller
 * 사용자 관련 API를 처리하는 컨트롤러 클래스
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 전체 사용자 조회
    @GetMapping
    public ApiResponseDto<List<UserDto.UserResponseDto>> getAllUsers() {
        List<UserDto.UserResponseDto> users = userService.findAll();
        return ApiResponseDto.success(HttpStatus.OK.getReasonPhrase(), users);
    }

    // ID로 사용자 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<UserDto.UserResponseDto>> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(
                        ApiResponseDto.success(HttpStatus.OK.getReasonPhrase(), user)
                ))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.fail(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase())));
    }

}
