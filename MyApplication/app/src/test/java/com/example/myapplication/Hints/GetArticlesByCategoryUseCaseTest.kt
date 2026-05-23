package com.example.myapplication.Hints

import com.example.domain.Hints.model.Article
import com.example.domain.Hints.repository.ArticleRepository
import com.example.domain.Hints.use_case.GetArticlesByCategoryUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class GetArticlesByCategoryUseCaseTest {

    private lateinit var repository: ArticleRepository
    private lateinit var useCase: GetArticlesByCategoryUseCase

    @Before
    fun setUp() {
        repository = Mockito.mock(ArticleRepository::class.java)
        useCase = GetArticlesByCategoryUseCase(repository)
    }

    @Test
    fun `invoke returns articles by category`() {
        runBlocking {
            val categoryId = 2
            val expected = emptyList<Article>()

            Mockito.`when`(repository.getArticlesByCategory(categoryId)).thenReturn(expected)

            val result = useCase(categoryId)

            assertEquals(expected, result)
            Mockito.verify(repository).getArticlesByCategory(categoryId)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke throws when repository throws`() {
        runBlocking {
            val categoryId = 2

            Mockito.`when`(repository.getArticlesByCategory(categoryId))
                .thenThrow(IllegalArgumentException("invalid category"))

            useCase(categoryId)
        }
    }
}