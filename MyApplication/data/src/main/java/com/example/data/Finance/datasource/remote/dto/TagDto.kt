package com.example.data.Finance.datasource.remote.dto

import java.math.BigDecimal

data class TagDto(
    val id: Int,
    val name: String,
    val totalAmountSpent: BigDecimal
)

data class CreateTagRequestDto(val name: String)
data class UpdateTagRequestDto(val name: String)

data class AssignTagRequestDto(val transactionIds: List<Int>)
