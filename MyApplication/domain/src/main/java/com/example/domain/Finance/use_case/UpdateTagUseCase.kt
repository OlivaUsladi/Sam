package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.Tag
import com.example.domain.Finance.repository.FinanceRepository
import java.math.BigDecimal

class UpdateTagUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(id: Int, name: String, monthlyLimit: BigDecimal? = null): Tag =
        repo.updateTag(id, name, monthlyLimit)
}