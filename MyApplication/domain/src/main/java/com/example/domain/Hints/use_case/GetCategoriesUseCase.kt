package com.example.domain.Hints.use_case

import com.example.domain.Hints.model.Category
import com.example.domain.Hints.repository.ArticleRepository

class GetCategoriesUseCase(
    private val repository: ArticleRepository
) {
    suspend operator fun invoke(): List<Category> {
        return repository.getCategories()
    }
}