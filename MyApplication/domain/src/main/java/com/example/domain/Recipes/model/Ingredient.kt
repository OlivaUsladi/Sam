package com.example.domain.Recipes.model

data class Ingredient (
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String //единица измерения (гр, шт, мл)
)