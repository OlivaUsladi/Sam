package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.Source
import com.example.domain.Finance.model.SourceType
import com.example.domain.Finance.repository.FinanceRepository

class UpdateSourceUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(id: Int, name: String, type: SourceType): Source =
        repo.updateSource(id, name, type)
}