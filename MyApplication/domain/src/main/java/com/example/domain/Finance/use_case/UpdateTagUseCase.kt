package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.Tag
import com.example.domain.Finance.repository.FinanceRepository

class UpdateTagUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(id: Int, name: String): Tag = repo.updateTag(id, name)
}