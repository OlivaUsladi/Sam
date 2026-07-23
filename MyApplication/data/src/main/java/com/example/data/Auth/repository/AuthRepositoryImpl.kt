package com.example.data.Auth.repository

import com.example.data.Auth.datasource.local.OnboardingStorage
import com.example.data.Auth.datasource.local.TokenStorage
import com.example.data.Auth.datasource.remote.api.AuthApiService
import com.example.data.Auth.datasource.remote.dto.ForgotPasswordRequestDto
import com.example.data.Auth.datasource.remote.dto.LoginRequestDto
import com.example.data.Auth.datasource.remote.dto.MeResponseDto
import com.example.data.Auth.datasource.remote.dto.RefreshRequestDto
import com.example.data.Auth.datasource.remote.dto.RegisterRequestDto
import com.example.data.Auth.datasource.remote.dto.ResetPasswordRequestDto
import com.example.domain.Auth.model.AuthSession
import com.example.domain.Auth.model.ForgotPasswordResult
import com.example.domain.Auth.model.User
import com.example.domain.Auth.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val tokenStorage: TokenStorage,
    private val onboardingStorage: OnboardingStorage
) : AuthRepository {

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        acceptedTermsVersion: String
    ): AuthSession {
        val resp = api.register(
            RegisterRequestDto(
                name = name,
                email = email,
                password = password,
                acceptedTermsVersion = acceptedTermsVersion
            )
        )
        persist(resp.accessToken, resp.refreshToken, resp.user)
        return AuthSession(
            accessToken = resp.accessToken,
            refreshToken = resp.refreshToken,
            expiresInSeconds = resp.expiresIn,
            user = resp.user.toDomain()
        )
    }

    override suspend fun login(email: String, password: String): AuthSession {
        val resp = api.login(LoginRequestDto(email, password))
        persist(resp.accessToken, resp.refreshToken, resp.user)
        return AuthSession(
            accessToken = resp.accessToken,
            refreshToken = resp.refreshToken,
            expiresInSeconds = resp.expiresIn,
            user = resp.user.toDomain()
        )
    }

    override suspend fun logout() {
        val refresh = tokenStorage.getRefreshToken()
        if (refresh != null) {
            runCatching { api.logout(RefreshRequestDto(refresh)) }
        }
        tokenStorage.clear()
    }

    override suspend fun me(): User = api.me().toDomain()

    override suspend fun forgotPassword(email: String): ForgotPasswordResult {
        val resp = api.forgotPassword(ForgotPasswordRequestDto(email))
        return ForgotPasswordResult(message = resp.message, resetToken = resp.resetToken)
    }

    override suspend fun resetPassword(resetToken: String, newPassword: String) {
        api.resetPassword(ResetPasswordRequestDto(resetToken, newPassword))
    }

    override suspend fun consentVersion(): String = api.consentVersion().version

    override fun isLoggedIn(): Boolean = tokenStorage.isLoggedIn()

    override fun currentUserId(): Int? = tokenStorage.getUserId()

    override fun isOnboardingCompleted(): Boolean = onboardingStorage.isCompleted()

    override fun markOnboardingCompleted() = onboardingStorage.markCompleted()

    override fun currentUserName(): String? = tokenStorage.getUserName()

    override fun currentUserEmail(): String? = tokenStorage.getUserEmail()

    private fun persist(access: String, refresh: String, user: MeResponseDto) {
        tokenStorage.saveTokens(access, refresh)
        tokenStorage.saveUserId(user.id)
        tokenStorage.saveUserInfo(user.name, user.email)
    }

    private fun MeResponseDto.toDomain() = User(id = id, name = name, email = email)
}