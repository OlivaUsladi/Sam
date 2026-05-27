package com.example.data.Finance.datasource.local

import com.example.domain.Finance.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate

data class ParsedTransaction(
    val name: String,
    val amount: BigDecimal,
    val type: TransactionType,
    val description: String?,
    val date: LocalDate,
)

data class ImportResult(
    val imported: Int,
    val skipped: Int,
)
