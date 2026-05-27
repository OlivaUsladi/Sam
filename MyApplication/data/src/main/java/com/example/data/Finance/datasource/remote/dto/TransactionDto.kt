package com.example.data.Finance.datasource.remote.dto

import java.math.BigDecimal

data class TransactionDto(
    val id: Int,
    val name: String,
    val amount: BigDecimal,
    val type: String,                 // income | expense
    val description: String?,
    val transactionDate: String,
    val sourceId: Int,
    val tagId: Int?
)

data class CreateTransactionRequestDto(
    val name: String,
    val amount: BigDecimal,
    val type: String,
    val description: String?,
    val transactionDate: String,
    val sourceId: Int,
    val tagId: Int?
)

data class UpdateTransactionRequestDto(
    val name: String,
    val amount: BigDecimal,
    val type: String,
    val description: String?,
    val transactionDate: String,
    val sourceId: Int,
    val tagId: Int?
)
