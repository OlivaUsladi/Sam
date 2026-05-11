package com.example.data.Recipes.di

import com.example.data.Recipes.datasource.local.RecipeLocalDataSource
import com.example.data.Recipes.datasource.local.RecipeLocalDataSourceImpl
import com.example.data.Recipes.datasource.remote.RecipeRemoteDataSource
import com.example.data.Recipes.datasource.remote.RetrofitClient
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