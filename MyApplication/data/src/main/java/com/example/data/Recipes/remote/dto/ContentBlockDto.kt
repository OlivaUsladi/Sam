package com.example.data.Recipes.remote.dto

data class ContentBlockDto(
    val type: String,
    val text: String?,
    val style: String?,
    val size: Int?,
    val area: String?,
    val imageId: Int?,
    val url: String?,
    val width: Int?,
    val height: Int?
)