package com.example.domain.Auth.use_case

import com.example.domain.Auth.model.AuthSession
import com.example.domain.Auth.repository.AuthRepository

class LoginUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AuthSession =
        repo.login(email, password)
}