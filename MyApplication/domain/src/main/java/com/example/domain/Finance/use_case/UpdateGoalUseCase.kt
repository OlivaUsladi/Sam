package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.Goal
import com.example.domain.Finance.repository.FinanceRepository
import java.math.BigDecimal
import java.time.LocalDate

class UpdateGoalUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(
        id: Int,
        name: String,
        description: String?,
        targetAmount: BigDecimal,
        currentAmount: BigDecimal,
        targetDate: LocalDate?,
        monthlyAmount: BigDecimal?,
    ): Goal = repo.updateGoal(id, name, description, targetAmount, currentAmount, targetDate, monthlyAmount)
}