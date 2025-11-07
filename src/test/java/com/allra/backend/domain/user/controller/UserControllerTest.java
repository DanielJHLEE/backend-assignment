package com.allra.backend.domain.user.controller;

import com.allra.backend.domain.user.entity.UserEntity;
import com.allra.backend.domain.user.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll(); // 테스트 전 기존 데이터 정리
        userRepository.save(new UserEntity(null, "이재홍", "jaehong@example.com", null));
        userRepository.save(new UserEntity(null, "박서준", "seo@example.com", null));
    }

    @Test
    @DisplayName("전체 유저 목록 조회 테스트")
    void getAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                // 기존: $.length() → 수정: $.data.length()
                .andExpect(jsonPath("$.data.length()").value(2))
                // 개별 항목 검증도 $.data[index].필드 로 변경
                .andExpect(jsonPath("$.data[0].name").value("이재홍"));
    }

    @DisplayName("특정 유저 상세 조회 테스트")
    void getUserById() throws Exception {
        Long id = userRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/users/" + id))
                .andExpect(status().isOk())
                // 기존: $.name → 수정: $.data.name
                .andExpect(jsonPath("$.data.name").value("이재홍"));
    }

    @Test
    @DisplayName("존재하지 않는 유저 ID 조회 시 404 반환")
    void getUserById_NotFound() throws Exception {
        mockMvc.perform(get("/api/users/99999")) // 존재하지 않는 ID
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll(); // 테스트 후 데이터 정리
    }
    
}
