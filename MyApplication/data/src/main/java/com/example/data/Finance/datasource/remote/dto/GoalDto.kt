package com.example.data.Finance.datasource.remote.dto

import java.math.BigDecimal

data class GoalDto(
    val id: Int,
    val name: String,
    val description: String?,
    val targetAmount: BigDecimal,
    val currentAmount: BigDecimal,
    val targetDate: String?,
    val monthlyAmount: BigDecimal?
)

data class CreateGoalRequestDto(
    val name: String,
    val description: String?,
    val targetAmount: BigDecimal,
    val targetDate: String?,
    val monthlyAmount: BigDecimal?
)

data class UpdateGoalRequestDto(
    val name: String,
    val description: String?,
    val targetAmount: BigDecimal,
    val currentAmount: BigDecimal,
    val targetDate: String?,
    val monthlyAmount: BigDecimal?
)
