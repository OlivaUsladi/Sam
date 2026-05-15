package com.example.myapplication.di.hints

import com.example.data.Hints.datasource.local.ArticleLocalDataSource
import com.example.data.Hints.datasource.local.ArticleLocalDataSourceImpl
import com.example.data.Hints.datasource.remote.ArticleRemoteDataSource
import com.example.data.Hints.datasource.remote.HintsRetrofitClient
import com.example.data.Hints.repository.ArticleRepositoryImpl
import com.example.domain.Hints.repository.ArticleRepository
import org.koin.dsl.module

val hintsDataModule = module {
    // Remote
    single { HintsRetrofitClient.hintsApiService}
    single { ArticleRemoteDataSource(get()) }

    // Repository
    single<ArticleRepository> { ArticleRepositoryImpl(remoteDataSource = get()) }
}
