package com.example.myapplication

import android.app.Application
import com.example.myapplication.di.appModule
import com.example.myapplication.di.hints.hintsDataModule
import com.example.myapplication.di.hints.hintsDomainModule
import com.example.myapplication.di.recipes.recipesDataModule
import com.example.myapplication.di.recipes.recipesDomainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(
                appModule,
                hintsDataModule,
                hintsDomainModule,
                recipesDataModule,
                recipesDomainModule
            )
        }
    }
}