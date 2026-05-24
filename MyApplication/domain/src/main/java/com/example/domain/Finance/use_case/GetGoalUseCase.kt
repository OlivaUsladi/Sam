package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.Goal
import com.example.domain.Finance.repository.FinanceRepository

class GetGoalUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(id: Int): Goal = repo.getGoal(id)
}