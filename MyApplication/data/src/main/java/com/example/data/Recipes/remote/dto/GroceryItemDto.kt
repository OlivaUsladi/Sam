package com.example.data.Recipes.remote.dto

data class GroceryItemDto(
    val id: Int,
    val groceryId: Int,
    val name: String,
    val defaultUnit: String
)