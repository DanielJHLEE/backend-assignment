package com.allra.backend.domain.user.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserDto {

   // 사용자 List/Detail 응답용DTO
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserResponseDto {
        private Long id;
        private String name;
        private String email;
        private LocalDateTime createdAt;
    

        // Entity -> DTO 변환 메서드
        public static UserResponseDto fromEntity(com.allra.backend.domain.user.entity.UserEntity user) {
            return UserResponseDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .createdAt(user.getCreatedAt())
                    .build();
        }
    }
}