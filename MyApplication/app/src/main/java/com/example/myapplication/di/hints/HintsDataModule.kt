package com.example.myapplication.di.hints

import com.example.data.Hints.datasource.remote.ArticleRemoteDataSource
import com.example.data.Recipes.datasource.remote.RetrofitClient
import com.example.data.Hints.repository.ArticleRepositoryImpl
import com.example.domain.Hints.repository.ArticleRepository
import org.koin.dsl.module

val hintsDataModule = module {
    single { RetrofitClient.articleApiService }
    single { ArticleRemoteDataSource(get()) }

    single<ArticleRepository> { ArticleRepositoryImpl(remoteDataSource = get()) }
}
