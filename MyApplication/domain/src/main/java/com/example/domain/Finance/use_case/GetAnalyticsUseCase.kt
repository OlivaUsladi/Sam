package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.Analytics
import com.example.domain.Finance.model.TransactionType
import com.example.domain.Finance.repository.FinanceRepository
import java.time.YearMonth

class GetAnalyticsUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(month: YearMonth, type: TransactionType): Analytics =
        repo.getAnalytics(month, type)
}
