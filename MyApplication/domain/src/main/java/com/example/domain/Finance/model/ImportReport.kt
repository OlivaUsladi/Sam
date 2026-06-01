package com.example.domain.Finance.model

import java.time.LocalDate
import java.time.YearMonth

data class ImportReport(
    val fileName: String,
    val sourceId: Int,
    val imported: Int,
    val skipped: Int,
    val total: Int,
    val firstDate: LocalDate? = null,
    val lastDate: LocalDate? = null,
    val suggestedMonth: YearMonth? = null,
)
