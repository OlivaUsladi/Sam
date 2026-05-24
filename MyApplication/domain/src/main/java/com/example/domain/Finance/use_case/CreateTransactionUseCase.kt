package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.Transaction
import com.example.domain.Finance.model.TransactionType
import com.example.domain.Finance.repository.FinanceRepository
import java.math.BigDecimal
import java.time.LocalDate

class CreateTransactionUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(
        name: String,
        amount: BigDecimal,
        type: TransactionType,
        description: String?,
        date: LocalDate,
        sourceId: Int,
        tagId: Int?,
    ): Transaction = repo.createTransaction(name, amount, type, description, date, sourceId, tagId)
}