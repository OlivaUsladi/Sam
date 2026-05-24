package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.Transaction
import com.example.domain.Finance.model.TransactionType
import com.example.domain.Finance.repository.FinanceRepository
import java.math.BigDecimal
import java.time.LocalDate

class GetTransactionsUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(
        type: TransactionType? = null,
        from: LocalDate? = null,
        to: LocalDate? = null,
    ): List<Transaction> = repo.getTransactions(type, from, to)
}