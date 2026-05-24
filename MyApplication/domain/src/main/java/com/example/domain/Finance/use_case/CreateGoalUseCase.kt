package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.Goal
import com.example.domain.Finance.repository.FinanceRepository
import java.math.BigDecimal
import java.time.LocalDate

class CreateGoalUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(
        name: String,
        description: String?,
        targetAmount: BigDecimal,
        targetDate: LocalDate?,
        monthlyAmount: BigDecimal?,
    ): Goal = repo.createGoal(name, description, targetAmount, targetDate, monthlyAmount)
}