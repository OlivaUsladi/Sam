package com.example.domain.Finance.model

data class ImportReport(
    val fileName: String,
    val sourceId: Int,
    val imported: Int,
    val skipped: Int,
    val total: Int,
)
