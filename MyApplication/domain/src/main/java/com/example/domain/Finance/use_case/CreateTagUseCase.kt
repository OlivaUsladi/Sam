package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.Tag
import com.example.domain.Finance.repository.FinanceRepository
import java.math.BigDecimal

class CreateTagUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(name: String, monthlyLimit: BigDecimal? = null): Tag =
        repo.createTag(name, monthlyLimit)
}
