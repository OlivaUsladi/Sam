package com.example.domain.Finance.use_case

import com.example.domain.Finance.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth


class ObserveAnalyticsTargetUseCase(
    private val repo: FinanceRepository,
) {
    operator fun invoke(): Flow<YearMonth?> = repo.observeAnalyticsTarget()
}

class ConsumeAnalyticsTargetUseCase(
    private val repo: FinanceRepository,
) {
    suspend operator fun invoke() = repo.consumeAnalyticsTarget()
}

class SyncNowUseCase(
    private val repo: FinanceRepository,
) {
    suspend operator fun invoke() = repo.syncNow()
}
