package com.example.myapplication.Hints

import com.example.domain.Hints.model.Like
import com.example.domain.Hints.repository.ArticleRepository
import com.example.domain.Hints.use_case.AddLikeUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class AddLikeUseCaseTest {

    private lateinit var repository: ArticleRepository
    private lateinit var addLikeUseCase: AddLikeUseCase

    @Before
    fun setUp() {
        repository = Mockito.mock(ArticleRepository::class.java)
        addLikeUseCase = AddLikeUseCase(repository)
    }

    @Test
    fun `toggle and return Like`() {
        runBlocking {
            val userId = 1
            val articleId = 100
            val expectedLike = Like(userId, articleId)

            Mockito.`when`(repository.addLike(userId, articleId)).thenReturn(expectedLike)

            val result = addLikeUseCase.invoke(userId, articleId)

            Assert.assertEquals(expectedLike, result)
            Assert.assertEquals(userId, result.userId)
            Assert.assertEquals(articleId, result.articleId)
            Mockito.verify(repository, Mockito.times(1)).addLike(userId, articleId)
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `Exception`() {
        runBlocking {
            val userId = 1
            val articleId = 100

            Mockito.`when`(repository.addLike(userId, articleId))
                .thenThrow(IllegalStateException("Already liked"))

            addLikeUseCase.invoke(userId, articleId)
        }
    }
}