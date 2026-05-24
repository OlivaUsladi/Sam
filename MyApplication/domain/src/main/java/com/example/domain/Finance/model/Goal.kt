package com.example.domain.Finance.model

import java.math.BigDecimal
import java.time.LocalDate


data class Goal(
    val id: Int,
    val name: String,
    val description: String?,
    val targetAmount: BigDecimal,
    val currentAmount: BigDecimal,
    val targetDate: LocalDate?,
    val monthlyAmount: BigDecimal?
)
