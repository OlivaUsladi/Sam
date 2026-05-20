package com.example.data.Auth.datasource.remote.dto

data class ForgotPasswordResponseDto(
    val message: String,
    val resetToken: String?
)