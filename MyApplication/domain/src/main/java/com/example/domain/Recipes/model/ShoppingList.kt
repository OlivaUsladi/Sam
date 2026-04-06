package com.example.domain.Recipes.model

import java.time.LocalDateTime

data class ShoppingList (
    val id: Int,
    val userId: Int,
    val name: String,
    val createdAt: LocalDateTime,
    val items: MutableList<ShoppingListItem>,
    val isCompleted: Boolean = false
)