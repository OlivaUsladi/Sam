package com.example.backend.controller;

import com.example.backend.dto.AuthResponseDto;
import com.example.backend.dto.ForgotPasswordRequestDto;
import com.example.backend.dto.ForgotPasswordResponseDto;
import com.example.backend.dto.LoginRequestDto;
import com.example.backend.dto.RefreshRequestDto;
import com.example.backend.dto.RegisterRequestDto;
import com.example.backend.dto.ResetPasswordRequestDto;
import com.example.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${app.security.consent.current-version:1.0}")
    private String currentConsentVersion;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDto register(@Valid @RequestBody RegisterRequestDto req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginRequestDto req) {
        return authService.login(req);
    }

    @PostMapping("/refresh")
    public AuthResponseDto refresh(@Valid @RequestBody RefreshRequestDto req) {
        return authService.refresh(req);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody(required = false) RefreshRequestDto req) {
        authService.logout(req == null ? null : req.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ForgotPasswordResponseDto forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto req) {
        return authService.forgotPassword(req.email());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequestDto req) {
        authService.resetPassword(req);
        return ResponseEntity.ok(Map.of("message", "Пароль успешно изменён"));
    }

    @GetMapping("/consent-version")
    public Map<String, String> consentVersion() {
        return Map.of("version", currentConsentVersion);
    }
}
