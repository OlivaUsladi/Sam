package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.Source
import com.example.domain.Finance.model.SourceType
import com.example.domain.Finance.repository.FinanceRepository

class GetSourcesUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(): List<Source> = repo.getSources()
}