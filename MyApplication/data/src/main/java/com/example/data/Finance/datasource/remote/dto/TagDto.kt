package com.example.data.Finance.datasource.remote.dto

import java.math.BigDecimal

data class TagDto(
    val id: Int,
    val name: String,
    val totalAmountSpent: BigDecimal,
    val monthlyLimit: BigDecimal? = null
)

data class CreateTagRequestDto(val name: String, val monthlyLimit: BigDecimal? = null)
data class UpdateTagRequestDto(val name: String, val monthlyLimit: BigDecimal? = null)

data class AssignTagRequestDto(val transactionIds: List<Int>)
