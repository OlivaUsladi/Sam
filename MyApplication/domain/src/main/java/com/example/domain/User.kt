package com.example.domain

data class User (
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: Boolean
)