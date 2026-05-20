package com.example.domain.Auth.use_case

import com.example.domain.Auth.repository.AuthRepository

class ResetPasswordUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(resetToken: String, newPassword: String) =
        repo.resetPassword(resetToken, newPassword)
}