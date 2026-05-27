package com.example.domain.Finance.use_case

import com.example.domain.Finance.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow

class ObserveFinanceChangesUseCase(private val repo: FinanceRepository) {
    operator fun invoke(): Flow<Long> = repo.observeDataVersion()
}
