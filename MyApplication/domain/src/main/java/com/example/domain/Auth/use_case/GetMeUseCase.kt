package com.example.domain.Auth.use_case

import com.example.domain.Auth.model.User
import com.example.domain.Auth.repository.AuthRepository

class GetMeUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(): User = repo.me()
}