package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.ImportReport
import com.example.domain.Finance.repository.FinanceRepository

class ImportBankReportUseCase(private val repo: FinanceRepository) {
    suspend operator fun invoke(
        fileName: String,
        sourceId: Int,
        content: ByteArray,
    ): ImportReport = repo.importBankReport(fileName, sourceId, content)
}
