package com.example.data.Auth.datasource.remote.dto

data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val acceptedTermsVersion: String
)