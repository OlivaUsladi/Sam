package com.example.backend.controller;

import com.example.backend.dto.MeResponseDto;
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @GetMapping("/me")
    public MeResponseDto me(@AuthenticationPrincipal AuthenticatedUser principal) {
        return authService.loadMe(principal.id());
    }
}
