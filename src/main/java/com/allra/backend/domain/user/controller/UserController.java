package com.allra.backend.domain.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.allra.backend.docs.swagger.SwaggerTags;
import com.allra.backend.domain.user.dto.UserDto;
import com.allra.backend.domain.user.service.UserService;
import com.allra.backend.global.dto.ApiResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 👤 User Controller
 * 사용자 관련 API를 처리하는 컨트롤러 클래스
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = SwaggerTags.USER_NAME, description = SwaggerTags.USER_DESC)
public class UserController {

    private final UserService userService;

    /**
     * 전체 사용자 목록 조회
     */
    @GetMapping
    @Operation(
        summary = "전체 사용자 조회",
        description = SwaggerTags.USER_GET_ALL_DESC
    )
    public ApiResponseDto<List<UserDto.UserResponseDto>> getAllUsers() {
        List<UserDto.UserResponseDto> users = userService.findAll();
        return ApiResponseDto.success(HttpStatus.OK.getReasonPhrase(), users);
    }

    /**
     * 사용자 단일 조회 (ID 기준)
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "사용자 단일 조회",
        description = SwaggerTags.USER_GET_DETAIL_DESC
    )
    public ResponseEntity<ApiResponseDto<UserDto.UserResponseDto>> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(
                        ApiResponseDto.success(HttpStatus.OK.getReasonPhrase(), user)
                ))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.fail(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase())));
    }
}
