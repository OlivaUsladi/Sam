package com.example.myapplication.di

import com.example.data.Hints.datasource.local.ArticleLocalDataSource
import com.example.data.Hints.datasource.local.ArticleLocalDataSourceImpl
import com.example.data.Hints.repository.ArticleRepositoryImpl
import com.example.domain.Hints.repository.ArticleRepository
import org.koin.dsl.module

val dataModule = module {
    single<ArticleLocalDataSource> { ArticleLocalDataSourceImpl() }
    single<ArticleRepository> { ArticleRepositoryImpl(get()) }
}