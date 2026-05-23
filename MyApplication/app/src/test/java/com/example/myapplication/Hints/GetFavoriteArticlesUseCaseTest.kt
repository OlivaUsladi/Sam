package com.example.myapplication.Hints

import com.example.domain.Hints.model.Article
import com.example.domain.Hints.repository.ArticleRepository
import com.example.domain.Hints.use_case.GetFavoriteArticlesUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class GetFavoriteArticlesUseCaseTest {

    private lateinit var repository: ArticleRepository
    private lateinit var useCase: GetFavoriteArticlesUseCase

    @Before
    fun setUp() {
        repository = Mockito.mock(ArticleRepository::class.java)
        useCase = GetFavoriteArticlesUseCase(repository)
    }

    @Test
    fun `invoke returns favorite articles`() {
        runBlocking {
            val userId = 1
            val expected = emptyList<Article>()

            Mockito.`when`(repository.getFavoriteArticles(userId)).thenReturn(expected)

            val result = useCase(userId)

            assertEquals(expected, result)
            Mockito.verify(repository).getFavoriteArticles(userId)
        }
    }

    @Test(expected = RuntimeException::class)
    fun `invoke throws when repository throws`() {
        runBlocking {
            val userId = 1
            Mockito.`when`(repository.getFavoriteArticles(userId)).thenThrow(RuntimeException("error"))
            useCase(userId)
        }
    }
}