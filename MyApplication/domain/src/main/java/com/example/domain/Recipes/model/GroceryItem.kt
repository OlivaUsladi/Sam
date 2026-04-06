package com.example.domain.Recipes.model

//Крупы - рис, гречка и т.д.
data class GroceryItem(
    val id: Int,
    val groceryId: Int,
    val name: String,
    val defaultUnit: String
)