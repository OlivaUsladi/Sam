package com.example.data.Auth.datasource.remote.dto

data class ResetPasswordRequestDto(
    val resetToken: String,
    val newPassword: String
)