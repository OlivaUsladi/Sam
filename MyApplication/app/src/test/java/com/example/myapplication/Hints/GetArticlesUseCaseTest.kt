package com.example.myapplication.Hints

import com.example.domain.Hints.model.Article
import com.example.domain.Hints.repository.ArticleRepository
import com.example.domain.Hints.use_case.GetArticlesUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class GetArticlesUseCaseTest {

    private lateinit var repository: ArticleRepository
    private lateinit var useCase: GetArticlesUseCase

    @Before
    fun setUp() {
        repository = Mockito.mock(ArticleRepository::class.java)
        useCase = GetArticlesUseCase(repository)
    }

    @Test
    fun `invoke returns articles from repository`() {
        runBlocking {
            val expected = emptyList<Article>()

            Mockito.`when`(repository.getArticles()).thenReturn(expected)

            val result = useCase()

            assertEquals(expected, result)
            Mockito.verify(repository).getArticles()
        }
    }

    @Test(expected = RuntimeException::class)
    fun `invoke throws when repository throws`() {
        runBlocking {
            Mockito.`when`(repository.getArticles()).thenThrow(RuntimeException("network error"))
            useCase()
        }
    }
}