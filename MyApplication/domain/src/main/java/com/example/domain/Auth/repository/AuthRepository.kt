package com.example.domain.Auth.repository

import com.example.domain.Auth.model.AuthSession
import com.example.domain.Auth.model.ForgotPasswordResult
import com.example.domain.Auth.model.User

interface AuthRepository {

    suspend fun register(
        name: String,
        email: String,
        password: String,
        acceptedTermsVersion: String
    ): AuthSession

    suspend fun login(email: String, password: String): AuthSession

    suspend fun logout()

    suspend fun me(): User

    suspend fun forgotPassword(email: String): ForgotPasswordResult

    suspend fun resetPassword(resetToken: String, newPassword: String)

    suspend fun consentVersion(): String

    fun isLoggedIn(): Boolean

    fun currentUserId(): Int?

    fun isOnboardingCompleted(): Boolean

    fun markOnboardingCompleted()
}
