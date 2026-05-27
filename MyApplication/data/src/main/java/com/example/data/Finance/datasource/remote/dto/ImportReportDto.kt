package com.example.data.Finance.datasource.remote.dto

data class ImportReportDto(
    val fileName: String,
    val sourceId: Int,
    val imported: Int,
    val skipped: Int,
    val total: Int,
)
