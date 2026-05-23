package com.example.myapplication.Hints

import com.example.domain.Hints.repository.ArticleRepository
import com.example.domain.Hints.use_case.IsArticleFavoriteUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class IsArticleFavoriteUseCaseTest {

    private lateinit var repository: ArticleRepository
    private lateinit var useCase: IsArticleFavoriteUseCase

    @Before
    fun setUp() {
        repository = Mockito.mock(ArticleRepository::class.java)
        useCase = IsArticleFavoriteUseCase(repository)
    }

    @Test
    fun `invoke returns true when article is favorite`() {
        runBlocking {
            val userId = 1
            val articleId = 3

            Mockito.`when`(repository.isArticleFavorite(userId, articleId)).thenReturn(true)

            val result = useCase(userId, articleId)

            assertTrue(result)
            Mockito.verify(repository).isArticleFavorite(userId, articleId)
        }
    }

    @Test
    fun `invoke returns false when article is not favorite`() {
        runBlocking {
            val userId = 1
            val articleId = 3

            Mockito.`when`(repository.isArticleFavorite(userId, articleId)).thenReturn(false)

            val result = useCase(userId, articleId)

            assertFalse(result)
            Mockito.verify(repository).isArticleFavorite(userId, articleId)
        }
    }
}