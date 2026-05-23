package com.example.myapplication.Hints

import com.example.domain.Hints.repository.ArticleRepository
import com.example.domain.Hints.use_case.GetLikesCountUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class GetLikesCountUseCaseTest {

    private lateinit var repository: ArticleRepository
    private lateinit var useCase: GetLikesCountUseCase

    @Before
    fun setUp() {
        repository = Mockito.mock(ArticleRepository::class.java)
        useCase = GetLikesCountUseCase(repository)
    }

    @Test
    fun `invoke returns likes count`() {
        runBlocking {
            val articleId = 9
            val expected = 5

            Mockito.`when`(repository.getLikesCount(articleId)).thenReturn(expected)

            val result = useCase(articleId)

            assertEquals(expected, result)
            Mockito.verify(repository).getLikesCount(articleId)
        }
    }

    @Test(expected = RuntimeException::class)
    fun `invoke throws when repository throws`() {
        runBlocking {
            val articleId = 9
            Mockito.`when`(repository.getLikesCount(articleId)).thenThrow(RuntimeException("error"))
            useCase(articleId)
        }
    }
}