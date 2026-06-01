package com.example.myapplication.di.finance

import com.example.domain.Auth.use_case.IsLoggedInUseCase
import com.example.domain.Finance.use_case.*
import org.koin.dsl.module

val financeDomainModule = module {
    factory { GetSourcesUseCase(get()) }
    factory { CreateSourceUseCase(get()) }
    factory { UpdateSourceUseCase(get()) }
    factory { DeleteSourceUseCase(get()) }

    factory { GetTagsUseCase(get()) }
    factory { CreateTagUseCase(get()) }
    factory { UpdateTagUseCase(get()) }
    factory { DeleteTagUseCase(get()) }
    factory { AssignTagToTransactionsUseCase(get()) }

    factory { GetTransactionsUseCase(get()) }
    factory { GetTransactionsByTagUseCase(get()) }
    factory { CreateTransactionUseCase(get()) }
    factory { UpdateTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }

    factory { GetGoalsUseCase(get()) }
    factory { GetGoalUseCase(get()) }
    factory { CreateGoalUseCase(get()) }
    factory { UpdateGoalUseCase(get()) }
    factory { DeleteGoalUseCase(get()) }
    factory { CalculateExpectedIncomeUseCase() }

    factory { GetAnalyticsUseCase(get()) }

    factory { IsLoggedInUseCase(get()) }

    factory { ObserveFinanceChangesUseCase(get()) }

    factory { ImportBankReportUseCase(get()) }

    factory { ObserveOfflineModeUseCase(get()) }
    factory { ObserveAnalyticsTargetUseCase(get()) }
    factory { ConsumeAnalyticsTargetUseCase(get()) }
    factory { SyncNowUseCase(get()) }
}
