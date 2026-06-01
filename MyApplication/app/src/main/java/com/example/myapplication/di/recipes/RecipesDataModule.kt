package com.example.myapplication.di.recipes

import com.example.data.Auth.datasource.local.TokenStorage
import com.example.data.Recipes.datasource.local.RecipeLocalDataSource
import com.example.data.Recipes.datasource.local.RecipeLocalDataSourceImpl
import com.example.data.Recipes.datasource.local.room.ShoppingListLocalDataSource
import com.example.data.Recipes.datasource.local.room.ShoppingListLocalDataSourceImpl
import com.example.data.Recipes.datasource.remote.RecipeRemoteDataSource
import com.example.data.Recipes.datasource.remote.RetrofitClient
import com.example.data.Recipes.datasource.remote.ShoppingListRemoteDataSource
import com.example.data.Recipes.repository.RecipeRepositoryImpl
import com.example.data.common.db.AppDatabase
import com.example.data.common.network.NetworkMonitor
import com.example.domain.Recipes.repository.RecipeRepository
import org.koin.dsl.module

val recipesDataModule = module {

    single<RecipeLocalDataSource> { RecipeLocalDataSourceImpl() }

    single { get<AppDatabase>().shoppingListDao() }
    single<ShoppingListLocalDataSource> { ShoppingListLocalDataSourceImpl(get()) }

    single { RetrofitClient.recipeApiService }
    single { RetrofitClient.shoppingListApiService }
    single { RecipeRemoteDataSource(get()) }
    single { ShoppingListRemoteDataSource(get()) }

    single<RecipeRepository> {
        RecipeRepositoryImpl(
            localDataSource = get(),
            remoteDataSource = get(),
            shoppingListRemoteDataSource = get(),
            shoppingLocal = get(),
            networkMonitor = get<NetworkMonitor>(),
            tokenStorage = get<TokenStorage>(),
        )
    }
}
