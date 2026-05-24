package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.Tag
import com.example.domain.Finance.repository.FinanceRepository

class GetTagsUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(): List<Tag> = repo.getTags()
}
