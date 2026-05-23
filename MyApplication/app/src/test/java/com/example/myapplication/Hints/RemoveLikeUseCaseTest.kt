package com.example.myapplication.Hints

import com.example.domain.Hints.repository.ArticleRepository
import com.example.domain.Hints.use_case.RemoveLikeUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class RemoveLikeUseCaseTest {

    private lateinit var repository: ArticleRepository
    private lateinit var useCase: RemoveLikeUseCase

    @Before
    fun setUp() {
        repository = Mockito.mock(ArticleRepository::class.java)
        useCase = RemoveLikeUseCase(repository)
    }

    @Test
    fun `invoke returns true when unlike succeeds`() {
        runBlocking {
            val userId = 1
            val articleId = 11

            Mockito.`when`(repository.removeLike(userId, articleId)).thenReturn(true)

            val result = useCase(userId, articleId)

            assertTrue(result)
            Mockito.verify(repository).removeLike(userId, articleId)
        }
    }

    @Test(expected = RuntimeException::class)
    fun `invoke throws when repository throws`() {
        runBlocking {
            val userId = 1
            val articleId = 11

            Mockito.`when`(repository.removeLike(userId, articleId)).thenThrow(RuntimeException("error"))

            useCase(userId, articleId)
        }
    }
}