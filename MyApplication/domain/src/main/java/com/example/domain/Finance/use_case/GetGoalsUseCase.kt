package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.Goal
import com.example.domain.Finance.repository.FinanceRepository
import java.math.BigDecimal
import java.time.LocalDate

class GetGoalsUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(): List<Goal> = repo.getGoals()
}
