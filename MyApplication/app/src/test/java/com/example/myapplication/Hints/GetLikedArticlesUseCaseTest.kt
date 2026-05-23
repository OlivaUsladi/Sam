package com.example.myapplication.Hints

import com.example.domain.Hints.model.Article
import com.example.domain.Hints.repository.ArticleRepository
import com.example.domain.Hints.use_case.GetLikedArticlesUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class GetLikedArticlesUseCaseTest {

    private lateinit var repository: ArticleRepository
    private lateinit var useCase: GetLikedArticlesUseCase

    @Before
    fun setUp() {
        repository = Mockito.mock(ArticleRepository::class.java)
        useCase = GetLikedArticlesUseCase(repository)
    }

    @Test
    fun `invoke returns liked articles`() {
        runBlocking {
            val userId = 1
            val expected = emptyList<Article>()

            Mockito.`when`(repository.getLikedArticles(userId)).thenReturn(expected)

            val result = useCase(userId)

            assertEquals(expected, result)
            Mockito.verify(repository).getLikedArticles(userId)
        }
    }

    @Test(expected = RuntimeException::class)
    fun `invoke throws when repository throws`() {
        runBlocking {
            val userId = 1
            Mockito.`when`(repository.getLikedArticles(userId)).thenThrow(RuntimeException("error"))
            useCase(userId)
        }
    }
}