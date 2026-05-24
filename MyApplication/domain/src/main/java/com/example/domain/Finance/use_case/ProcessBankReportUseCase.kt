package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.BankReport
import com.example.domain.Finance.repository.FinanceRepository

class ProcessBankReportUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(id: Int): BankReport = repo.processBankReport(id)
}
