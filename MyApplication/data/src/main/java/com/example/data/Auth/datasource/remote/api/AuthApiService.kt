package com.example.data.Auth.datasource.remote.api

import com.example.data.Auth.datasource.remote.dto.AuthResponseDto
import com.example.data.Auth.datasource.remote.dto.ConsentVersionResponseDto
import com.example.data.Auth.datasource.remote.dto.ForgotPasswordRequestDto
import com.example.data.Auth.datasource.remote.dto.ForgotPasswordResponseDto
import com.example.data.Auth.datasource.remote.dto.LoginRequestDto
import com.example.data.Auth.datasource.remote.dto.MeResponseDto
import com.example.data.Auth.datasource.remote.dto.MessageResponseDto
import com.example.data.Auth.datasource.remote.dto.RefreshRequestDto
import com.example.data.Auth.datasource.remote.dto.RegisterRequestDto
import com.example.data.Auth.datasource.remote.dto.ResetPasswordRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/register")
    suspend fun register(@Body req: RegisterRequestDto): AuthResponseDto

    @POST("api/auth/login")
    suspend fun login(@Body req: LoginRequestDto): AuthResponseDto

    @POST("api/auth/refresh")
    suspend fun refresh(@Body req: RefreshRequestDto): AuthResponseDto

    @POST("api/auth/logout")
    suspend fun logout(@Body req: RefreshRequestDto)

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body req: ForgotPasswordRequestDto): ForgotPasswordResponseDto

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body req: ResetPasswordRequestDto): MessageResponseDto

    @GET("api/auth/consent-version")
    suspend fun consentVersion(): ConsentVersionResponseDto

    @GET("api/users/me")
    suspend fun me(): MeResponseDto
}
