package com.example.domain.Auth.use_case

import com.example.domain.Auth.repository.AuthRepository

class ConsentVersionUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(): String = repo.consentVersion()
}