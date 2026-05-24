package com.example.domain.Finance.use_case

import com.example.domain.Finance.repository.FinanceRepository

class DeleteTransactionUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(id: Int) = repo.deleteTransaction(id)
}
