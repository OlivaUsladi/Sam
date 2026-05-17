package com.example.backend.dto;


 //Пока что флоу без реального email. После SMTP поле надо убрать и токен пользователь получит в письме.

public record ForgotPasswordResponseDto(
        String message,
        String resetToken
) {
}
