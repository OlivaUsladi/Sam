package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.BankReport
import com.example.domain.Finance.repository.FinanceRepository

class UploadBankReportUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(fileName: String, sizeBytes: Long, content: ByteArray): BankReport =
        repo.uploadBankReport(fileName, sizeBytes, content)
}