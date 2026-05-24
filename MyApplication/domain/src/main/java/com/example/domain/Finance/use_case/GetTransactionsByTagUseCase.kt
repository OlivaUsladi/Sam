package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.Transaction
import com.example.domain.Finance.repository.FinanceRepository

class GetTransactionsByTagUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(tagId: Int): List<Transaction> =
        repo.getTransactionsByTag(tagId)
}