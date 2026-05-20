package com.example.myapplication.di.auth

import com.example.data.Auth.datasource.local.OnboardingStorage
import com.example.data.Auth.datasource.local.TokenStorage
import com.example.data.Auth.repository.AuthRepositoryImpl
import com.example.data.Recipes.datasource.remote.RetrofitClient
import com.example.domain.Auth.repository.AuthRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val authDataModule = module {
    single { TokenStorage(androidContext()) }
    single { OnboardingStorage(androidContext()) }

    single<AuthRepository> {
        AuthRepositoryImpl(
            api = RetrofitClient.authApiService,
            tokenStorage = get(),
            onboardingStorage = get()
        )
    }
}
