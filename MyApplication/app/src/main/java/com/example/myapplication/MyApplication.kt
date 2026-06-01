package com.example.myapplication

import android.app.Application
import com.example.data.Auth.datasource.local.TokenStorage
import com.example.data.Recipes.datasource.remote.RetrofitClient
import com.example.data.common.network.NetworkMonitor
import com.example.data.common.sync.SyncManager
import com.example.domain.Finance.repository.FinanceRepository
import com.example.domain.Recipes.repository.RecipeRepository
import com.example.myapplication.di.appModule
import com.example.myapplication.di.auth.authDataModule
import com.example.myapplication.di.auth.authDomainModule
import com.example.myapplication.di.finance.financeDataModule
import com.example.myapplication.di.finance.financeDomainModule
import com.example.myapplication.di.hints.hintsDataModule
import com.example.myapplication.di.hints.hintsDomainModule
import com.example.myapplication.di.recipes.recipesDataModule
import com.example.myapplication.di.recipes.recipesDomainModule
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class MyApplication : Application() {

    private lateinit var syncManager: SyncManager

    override fun onCreate() {
        super.onCreate()

        val tokenStorage = TokenStorage(this)
        RetrofitClient.init(tokenStorage)

        startKoin {
            androidContext(this@MyApplication)
            modules(
                appModule,
                authDataModule,
                authDomainModule,
                hintsDataModule,
                hintsDomainModule,
                recipesDataModule,
                recipesDomainModule,
                financeDataModule,
                financeDomainModule,
            )
        }

        syncManager = SyncManager(
            networkMonitor = get<NetworkMonitor>(),
            financeRepository = get<FinanceRepository>(),
            recipeRepository = get<RecipeRepository>(),
        )
        syncManager.start()
    }
}
