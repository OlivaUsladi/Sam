package com.example.data.Recipes.di

import com.example.data.Recipes.datasource.local.RecipeLocalDataSource
import com.example.data.Recipes.datasource.local.RecipeLocalDataSourceImpl
import com.example.data.Recipes.remote.RecipeRemoteDataSource
import com.example.data.Recipes.remote.RetrofitClient
import com.example.data.Recipes.remote.api.RecipeApiService
import com.example.data.Recipes.repository.RecipeRepositoryImpl
import com.example.domain.Recipes.repository.RecipeRepository
import org.koin.dsl.module

val recipesDataModule = module {
    // Local
    single<RecipeLocalDataSource> { RecipeLocalDataSourceImpl() }

    // Remote
    single { RetrofitClient.recipeApiService }
    single { RecipeRemoteDataSource(get()) }

    // Repository
    single<RecipeRepository> { RecipeRepositoryImpl(get(), get()) }
}