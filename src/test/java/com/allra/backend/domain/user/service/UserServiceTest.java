package com.allra.backend.domain.user.service;


import com.allra.backend.domain.user.dto.UserDto;
import com.allra.backend.domain.user.entity.UserEntity;
import com.allra.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserService userService = new UserService(userRepository);

    @Test
    @DisplayName("유저 전체 조회 로직이 정상적으로 반환되는지 테스트")
    void findAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(
                new UserEntity(1L, "이재홍", "jaehong@example.com", null),
                new UserEntity(2L, "박서준", "seo@example.com", null)
        ));

        List<UserDto.UserResponseDto> users = userService.findAll();

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getName()).isEqualTo("이재홍");
    }

    @Test
    @DisplayName("유저 상세 조회 로직이 정상적으로 동작하는지 테스트")
    void findUserById() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(new UserEntity(1L, "이재홍", "jaehong@example.com", null)));

        Optional<UserDto.UserResponseDto> user = userService.findById(1L);
        assertThat(user).isPresent();
        assertThat(user.get().getName()).isEqualTo("이재홍");
    }
}
