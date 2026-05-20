package com.example.domain.Auth.use_case

import com.example.domain.Auth.repository.AuthRepository

class MarkOnboardingCompletedUseCase(private val repo: AuthRepository) {
    operator fun invoke() = repo.markOnboardingCompleted()
}
