package com.example.domain.Auth.use_case

import com.example.domain.Auth.repository.AuthRepository

class IsOnboardingCompletedUseCase(private val repo: AuthRepository) {
    operator fun invoke(): Boolean = repo.isOnboardingCompleted()
}