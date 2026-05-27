package com.example.data.Finance.datasource.remote.dto

data class SourceDto(
    val id: Int,
    val name: String,
    val type: String
)

data class CreateSourceRequestDto(val name: String, val type: String)
data class UpdateSourceRequestDto(val name: String, val type: String)
