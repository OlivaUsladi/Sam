package com.example.data.Recipes.datasource.remote.dto

data class GroceryItemDto(
    val id: Int,
    val groceryId: Int,
    val name: String,
    val defaultUnit: String
)