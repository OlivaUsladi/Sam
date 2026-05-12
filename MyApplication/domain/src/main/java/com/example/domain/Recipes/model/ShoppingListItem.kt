package com.example.domain.Recipes.model

data class ShoppingListItem (
    val id: Int,
    val description: String,
    val isChecked: Boolean = false,
    val quantity: Double? = null,
    val unit: String? = null
)