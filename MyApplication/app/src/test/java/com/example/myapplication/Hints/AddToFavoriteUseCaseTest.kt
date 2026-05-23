package com.example.myapplication.Hints

import com.example.domain.Hints.model.Favorite
import com.example.domain.Hints.repository.ArticleRepository
import com.example.domain.Hints.use_case.AddToFavoriteUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class AddToFavoriteUseCaseTest {

    private lateinit var repository: ArticleRepository
    private lateinit var useCase: AddToFavoriteUseCase

    @Before
    fun setUp() {
        repository = Mockito.mock(ArticleRepository::class.java)
        useCase = AddToFavoriteUseCase(repository)
    }

    @Test
    fun `invoke returns favorite from repository`() {
        runBlocking {
            val userId = 1
            val articleId = 101
            val expected = Favorite(userId, articleId)

            Mockito.`when`(repository.addToFavorites(userId, articleId)).thenReturn(expected)

            val result = useCase(userId, articleId)

            assertEquals(expected, result)
            Mockito.verify(repository).addToFavorites(userId, articleId)
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `invoke throws when repository throws`() {
        runBlocking {
            val userId = 1
            val articleId = 101

            Mockito.`when`(repository.addToFavorites(userId, articleId))
                .thenThrow(IllegalStateException("Already favorite"))

            useCase(userId, articleId)
        }
    }
}