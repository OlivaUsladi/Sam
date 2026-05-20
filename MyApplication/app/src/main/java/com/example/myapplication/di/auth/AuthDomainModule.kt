package com.example.myapplication.di.auth

import com.example.domain.Auth.use_case.ConsentVersionUseCase
import com.example.domain.Auth.use_case.ForgotPasswordUseCase
import com.example.domain.Auth.use_case.GetMeUseCase
import com.example.domain.Auth.use_case.IsLoggedInUseCase
import com.example.domain.Auth.use_case.IsOnboardingCompletedUseCase
import com.example.domain.Auth.use_case.LoginUseCase
import com.example.domain.Auth.use_case.LogoutUseCase
import com.example.domain.Auth.use_case.MarkOnboardingCompletedUseCase
import com.example.domain.Auth.use_case.RegisterUseCase
import com.example.domain.Auth.use_case.ResetPasswordUseCase
import org.koin.dsl.module

val authDomainModule = module {
    factory { ConsentVersionUseCase(get()) }
    factory { ForgotPasswordUseCase(get()) }
    factory { GetMeUseCase(get()) }
    factory { IsLoggedInUseCase(get()) }
    factory { IsOnboardingCompletedUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { MarkOnboardingCompletedUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { ResetPasswordUseCase(get()) }
}