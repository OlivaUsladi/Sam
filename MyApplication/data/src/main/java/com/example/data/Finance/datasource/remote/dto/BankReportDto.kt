package com.example.data.Finance.datasource.remote.dto

data class BankReportDto(
    val id: Int,
    val fileName: String,
    val sizeBytes: Long,
    val processed: Boolean,
    val uploadedAt: String
)
