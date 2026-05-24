package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.BankReport
import com.example.domain.Finance.repository.FinanceRepository

class GetBankReportsUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(): List<BankReport> = repo.getBankReports()
}