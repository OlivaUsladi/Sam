package com.example.domain.Auth.use_case

import com.example.domain.Auth.model.AuthSession
import com.example.domain.Auth.repository.AuthRepository

class RegisterUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        acceptedTermsVersion: String
    ): AuthSession {
        return repo.register(name, email, password, acceptedTermsVersion)
    }
}