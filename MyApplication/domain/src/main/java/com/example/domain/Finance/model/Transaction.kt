package com.example.domain.Finance.model

import java.math.BigDecimal
import java.time.LocalDate


data class Transaction(
    val id: Int,
    val name: String,
    val amount: BigDecimal,
    val type: TransactionType,
    val description: String?,
    val date: LocalDate,
    val sourceId: Int,
    val tagId: Int?
)

enum class TransactionType(val raw: String) {
    INCOME("income"),
    EXPENSE("expense");

    companion object {
        fun fromRaw(raw: String): TransactionType =
            entries.firstOrNull { it.raw == raw } ?: INCOME
    }
}
