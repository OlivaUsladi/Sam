package com.example.myapplication.di.recipes

import com.example.data.Recipes.datasource.local.RecipeLocalDataSource
import com.example.data.Recipes.datasource.local.RecipeLocalDataSourceImpl
import com.example.data.Recipes.repository.RecipeRepositoryImpl
import com.example.domain.Recipes.repository.RecipeRepository
import org.koin.dsl.module

val recipesDataModule = module {
    single<RecipeLocalDataSource> { RecipeLocalDataSourceImpl() }
    single<RecipeRepository> { RecipeRepositoryImpl(get()) }
}