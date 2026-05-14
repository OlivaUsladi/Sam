package com.example.data.Recipes.datasource.remote.dto

data class ShoppingListItemResponseDto(
    val id: Int,
    val description: String,
    val isChecked: Boolean,
    val quantity: Double?,
    val unit: String?
)
