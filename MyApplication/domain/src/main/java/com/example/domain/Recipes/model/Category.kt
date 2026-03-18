package com.example.domain.Recipes.model

//Первое блюдо, острое и т.д.
data class Category(
    val id: Int,
    val name: String,
    val description: String? = null
)
