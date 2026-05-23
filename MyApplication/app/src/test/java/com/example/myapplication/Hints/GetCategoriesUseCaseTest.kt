package com.example.myapplication.Hints

import com.example.domain.Hints.model.Category
import com.example.domain.Hints.repository.ArticleRepository
import com.example.domain.Hints.use_case.GetCategoriesUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class GetCategoriesUseCaseTest {

    private lateinit var repository: ArticleRepository
    private lateinit var useCase: GetCategoriesUseCase

    @Before
    fun setUp() {
        repository = Mockito.mock(ArticleRepository::class.java)
        useCase = GetCategoriesUseCase(repository)
    }

    @Test
    fun `invoke returns categories from repository`() {
        runBlocking {
            val expected = listOf(Category(
                1,
                "Category",
                "Just Description pupupu"
            ))

            Mockito.`when`(repository.getCategories()).thenReturn(expected)

            val result = useCase()

            assertEquals(expected, result)
            Mockito.verify(repository).getCategories()
        }
    }

    @Test(expected = RuntimeException::class)
    fun `invoke throws when repository throws`() {
        runBlocking {
            Mockito.`when`(repository.getCategories()).thenThrow(RuntimeException("error"))
            useCase()
        }
    }
}