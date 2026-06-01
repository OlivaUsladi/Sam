package com.example.domain.Finance.use_case

import com.example.domain.Finance.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow

class ObserveOfflineModeUseCase(
    private val repo: FinanceRepository,
) {
    operator fun invoke(): Flow<Boolean> = repo.observeOfflineMode()
}
