package com.example.myapplication.Hints

import com.example.domain.Hints.repository.ArticleRepository
import com.example.domain.Hints.use_case.RemoveFromFavoriteUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class RemoveFromFavoriteUseCaseTest {

    private lateinit var repository: ArticleRepository
    private lateinit var useCase: RemoveFromFavoriteUseCase

    @Before
    fun setUp() {
        repository = Mockito.mock(ArticleRepository::class.java)
        useCase = RemoveFromFavoriteUseCase(repository)
    }

    @Test
    fun `invoke returns true when removal succeeds`() {
        runBlocking {
            val userId = 1
            val articleId = 8

            Mockito.`when`(repository.removeFromFavorites(userId, articleId)).thenReturn(true)

            val result = useCase(userId, articleId)

            assertTrue(result)
            Mockito.verify(repository).removeFromFavorites(userId, articleId)
        }
    }

    @Test(expected = RuntimeException::class)
    fun `invoke throws when repository throws`() {
        runBlocking {
            val userId = 1
            val articleId = 8

            Mockito.`when`(repository.removeFromFavorites(userId, articleId)).thenThrow(RuntimeException("error"))

            useCase(userId, articleId)
        }
    }
}