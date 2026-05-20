package com.example.data.Auth.datasource.remote.dto

data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val user: MeResponseDto
)