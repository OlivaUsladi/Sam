package com.example.domain.Auth.model

data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val expiresInSeconds: Long,
    val user: User
)

data class ForgotPasswordResult(
    val message: String,
    val resetToken: String?
)
