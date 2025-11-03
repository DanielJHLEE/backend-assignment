package com.allra.backend.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/users/me")
    public ResponseEntity<String> getCurrentUser() {
        // Placeholder implementation
        return ResponseEntity.ok("usercontroller진입확인");
    }
}
