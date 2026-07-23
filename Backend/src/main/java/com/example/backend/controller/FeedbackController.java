package com.example.backend.controller;

import com.example.backend.dto.FeedbackRequestDto;
import com.example.backend.entity.FeedbackEntity;
import com.example.backend.entity.UserEntity;
import com.example.backend.repository.FeedbackRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.security.EmailCipherService;
import com.example.backend.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final EmailCipherService emailCipher;
    private final EmailService emailService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> submit(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody FeedbackRequestDto req) {

        UserEntity user = userRepository.findById(principal.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        FeedbackEntity entity = FeedbackEntity.builder()
                .userId(principal.id())
                .subject(req.subject())
                .message(req.message())
                .build();
        feedbackRepository.save(entity);

        String userEmail = emailCipher.decrypt(user.getEmailEncrypted());
        emailService.sendFeedbackNotification(user.getName(), userEmail, req.subject(), req.message());

        return Map.of("message", "Сообщение отправлено. Спасибо за обратную связь!");
    }
}
