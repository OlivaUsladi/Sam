package com.example.domain.Auth.use_case

import com.example.domain.Auth.model.ForgotPasswordResult
import com.example.domain.Auth.repository.AuthRepository

class ForgotPasswordUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String): ForgotPasswordResult =
        repo.forgotPassword(email)
}