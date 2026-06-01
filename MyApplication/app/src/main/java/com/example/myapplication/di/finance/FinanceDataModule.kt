package com.example.myapplication.di.finance

import com.example.data.Finance.datasource.local.FinanceLocalDataSource
import com.example.data.Finance.datasource.local.FinanceLocalDataSourceImpl
import com.example.data.Finance.datasource.remote.FinanceRemoteDataSource
import com.example.data.Finance.repository.FinanceRepositoryImpl
import com.example.data.Recipes.datasource.remote.RetrofitClient
import com.example.data.common.db.AppDatabase
import com.example.data.common.network.NetworkMonitor
import com.example.domain.Finance.repository.FinanceRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val financeDataModule = module {

    single { AppDatabase.build(androidContext()) }
    single { NetworkMonitor(androidContext()) }

    single { get<AppDatabase>().sourceDao() }
    single { get<AppDatabase>().tagDao() }
    single { get<AppDatabase>().goalDao() }
    single { get<AppDatabase>().transactionDao() }

    single<FinanceLocalDataSource> {
        FinanceLocalDataSourceImpl(
            sourceDao = get(), tagDao = get(),
            goalDao = get(), transactionDao = get(),
        )
    }

    single { RetrofitClient.financeApiService }
    single { FinanceRemoteDataSource(get()) }

    single<FinanceRepository> {
        FinanceRepositoryImpl(
            remote = get(),
            local = get(),
            tokenStorage = get(),
            networkMonitor = get(),
        )
    }
}
