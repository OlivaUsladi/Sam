package com.example.data.Recipes.datasource.remote.dto 

data class ShoppingListResponseDto(
    val id: Int,
    val userId: Int,
    val name: String,
    val createdAt: String,
    val isCompleted: Boolean,
    val items: List<ShoppingListItemResponseDto>
)