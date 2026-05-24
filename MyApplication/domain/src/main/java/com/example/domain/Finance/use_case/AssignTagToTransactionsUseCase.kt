package com.example.domain.Finance.use_case

import com.example.domain.Finance.repository.FinanceRepository

class AssignTagToTransactionsUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(tagId: Int, transactionIds: List<Int>) =
        repo.assignTagToTransactions(tagId, transactionIds)
}