package com.example.myapplication.Hints.usecase

import com.example.domain.Hints.model.Like
import com.example.domain.Hints.repository.ArticleRepository
import com.example.domain.Hints.use_case.AddLikeUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class AddLikeUseCaseTest {

    private lateinit var repository: ArticleRepository
    private lateinit var useCase: AddLikeUseCase

    @Before
    fun setUp() {
        repository = Mockito.mock(ArticleRepository::class.java)
        useCase = AddLikeUseCase(repository)
    }

    @Test
    fun `invoke returns like from repository`() {
        runBlocking {
            val userId = 1
            val articleId = 100
            val expected = Like(userId, articleId)

            Mockito.`when`(repository.addLike(userId, articleId)).thenReturn(expected)

            val result = useCase(userId, articleId)

            assertEquals(expected, result)
            Mockito.verify(repository).addLike(userId, articleId)
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `invoke throws when repository throws`() {
        runBlocking {
            val userId = 1
            val articleId = 100

            Mockito.`when`(repository.addLike(userId, articleId))
                .thenThrow(IllegalStateException("Already liked"))

            useCase(userId, articleId)
        }
    }
}
