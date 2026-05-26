package com.example.myapplication.di.finance

import com.example.data.Finance.datasource.local.FinanceLocalDataSource
import com.example.data.Finance.repository.FinanceRepositoryImpl
import com.example.domain.Finance.repository.FinanceRepository
import org.koin.dsl.module

val financeDataModule = module {

    // -------- Local --------
    single { FinanceLocalDataSource() }
    single { FinanceRepositoryImpl(get()) }

    // -------- Remote --------
    //single { RetrofitFinanceClient.financeApiService }
    //single { FinanceRemoteDataSource(get()) }
    //single { FinanceRepositoryImpl(get()) }

    single<FinanceRepository> {
        get<FinanceRepositoryImpl>()
    }
}
