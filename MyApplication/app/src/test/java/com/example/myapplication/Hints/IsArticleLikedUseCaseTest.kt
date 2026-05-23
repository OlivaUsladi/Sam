package com.example.myapplication.Hints

import com.example.domain.Hints.repository.ArticleRepository
import com.example.domain.Hints.use_case.IsArticleLikedUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class IsArticleLikedUseCaseTest {

    private lateinit var repository: ArticleRepository
    private lateinit var useCase: IsArticleLikedUseCase

    @Before
    fun setUp() {
        repository = Mockito.mock(ArticleRepository::class.java)
        useCase = IsArticleLikedUseCase(repository)
    }

    @Test
    fun `invoke returns true when article is liked`() {
        runBlocking {
            val userId = 1
            val articleId = 4

            Mockito.`when`(repository.isArticleLiked(userId, articleId)).thenReturn(true)

            val result = useCase(userId, articleId)

            assertTrue(result)
            Mockito.verify(repository).isArticleLiked(userId, articleId)
        }
    }

    @Test
    fun `invoke returns false when article is not liked`() {
        runBlocking {
            val userId = 1
            val articleId = 4

            Mockito.`when`(repository.isArticleLiked(userId, articleId)).thenReturn(false)

            val result = useCase(userId, articleId)

            assertFalse(result)
            Mockito.verify(repository).isArticleLiked(userId, articleId)
        }
    }
}