package com.example.myapplication.Hints

import com.example.domain.Hints.model.ArticleContent
import com.example.domain.Hints.repository.ArticleRepository
import com.example.domain.Hints.use_case.GetArticleContentUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class GetArticleContentUseCaseTest {

    private lateinit var repository: ArticleRepository
    private lateinit var useCase: GetArticleContentUseCase

    @Before
    fun setUp() {
        repository = Mockito.mock(ArticleRepository::class.java)
        useCase = GetArticleContentUseCase(repository)
    }

    @Test
    fun `invoke returns article content by id`() {
        runBlocking {
            val articleId = 10
            val expected = ArticleContent(
                10,
                emptyList(),
                ""
            )

            Mockito.`when`(repository.getArticleContent(articleId)).thenReturn(expected)

            val result = useCase(articleId)

            assertEquals(expected, result)
            Mockito.verify(repository).getArticleContent(articleId)
        }
    }

    @Test(expected = NoSuchElementException::class)
    fun `invoke throws when repository throws`() {
        runBlocking {
            val articleId = 10

            Mockito.`when`(repository.getArticleContent(articleId))
                .thenThrow(NoSuchElementException("not found"))

            useCase(articleId)
        }
    }
}