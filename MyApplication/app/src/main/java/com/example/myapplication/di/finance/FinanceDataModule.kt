package com.example.myapplication.di.finance

import com.example.data.Finance.datasource.remote.FinanceRemoteDataSource
import com.example.data.Finance.repository.FinanceRepositoryImpl
import com.example.data.Recipes.datasource.remote.RetrofitClient
import com.example.domain.Finance.repository.FinanceRepository
import org.koin.dsl.module

val financeDataModule = module {

    // -------- Local --------
    //single { FinanceLocalDataSource() }
    //single { FinanceRepositoryImpl(get()) }

    // -------- Remote --------
    single { RetrofitClient.financeApiService}
    single { FinanceRemoteDataSource(get()) }



    single<FinanceRepository> {
        FinanceRepositoryImpl(get())
    }
}
