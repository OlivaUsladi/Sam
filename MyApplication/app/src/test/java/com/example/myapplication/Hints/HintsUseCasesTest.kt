package com.example.domain.Hints.use_case

import com.example.domain.Hints.model.*
import com.example.domain.Hints.repository.ArticleRepository
import com.example.domain.Hints.model.Category
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class HintsUseCasesTest {

    private val repo: ArticleRepository = mock()

    @Test
    fun `GetCategoriesUseCase returns repo categories`() = runTest {
        val cats = listOf(Category(
            1, "Финансы", "Статьи о финансах"),
            Category(2, "Здоровье", "Статьи о здоровье"))
        whenever(repo.getCategories()).thenReturn(cats)
        assertEquals(cats, GetCategoriesUseCase(repo).invoke())
    }

    @Test
    fun `GetArticlesUseCase returns all articles`() = runTest {
        val list = listOf(mock<Article>(), mock<Article>())
        whenever(repo.getArticles()).thenReturn(list)
        assertEquals(list, GetArticlesUseCase(repo).invoke())
    }

    @Test
    fun `GetArticlesByCategoryUseCase filters by category`() = runTest {
        whenever(repo.getArticlesByCategory(5)).thenReturn(listOf())
        GetArticlesByCategoryUseCase(repo).invoke(5)
        verify(repo).getArticlesByCategory(5)
    }

    @Test
    fun `SearchArticlesUseCase calls search`() = runTest {
        whenever(repo.searchArticles("ипотека")).thenReturn(listOf())
        SearchArticlesUseCase(repo).invoke("ипотека")
        verify(repo).searchArticles("ипотека")
    }

    @Test
    fun `GetArticleContentUseCase returns content`() = runTest {
        val content = mock<ArticleContent>()
        whenever(repo.getArticleContent(1)).thenReturn(content)
        assertEquals(content, GetArticleContentUseCase(repo).invoke(1))
    }

    @Test
    fun `AddToFavoriteUseCase delegates`() = runTest {
        val fav = mock<Favorite>()
        whenever(repo.addToFavorites(1, 10)).thenReturn(fav)
        assertEquals(fav, AddToFavoriteUseCase(repo).invoke(1, 10))
    }

    @Test
    fun `RemoveFromFavoriteUseCase delegates`() = runTest {
        RemoveFromFavoriteUseCase(repo).invoke(1, 10)
        verify(repo).removeFromFavorites(1, 10)
    }

    @Test
    fun `GetFavoriteArticlesUseCase returns list`() = runTest {
        whenever(repo.getFavoriteArticles(1)).thenReturn(listOf())
        GetFavoriteArticlesUseCase(repo).invoke(1)
        verify(repo).getFavoriteArticles(1)
    }

    @Test
    fun `IsArticleFavoriteUseCase returns flag`() = runTest {
        whenever(repo.isArticleFavorite(1, 5)).thenReturn(true)
        assertTrue(IsArticleFavoriteUseCase(repo).invoke(1, 5))
    }

    @Test
    fun `AddLikeUseCase delegates`() = runTest {
        val like = mock<Like>()
        whenever(repo.addLike(1, 10)).thenReturn(like)
        assertEquals(like, AddLikeUseCase(repo).invoke(1, 10))
    }

    @Test
    fun `RemoveLikeUseCase delegates`() = runTest {
        RemoveLikeUseCase(repo).invoke(1, 10)
        verify(repo).removeLike(1, 10)
    }

    @Test
    fun `GetLikedArticlesUseCase returns list`() = runTest {
        whenever(repo.getLikedArticles(1)).thenReturn(listOf())
        GetLikedArticlesUseCase(repo).invoke(1)
        verify(repo).getLikedArticles(1)
    }

    @Test
    fun `IsArticleLikedUseCase returns flag`() = runTest {
        whenever(repo.isArticleLiked(1, 10)).thenReturn(false)
        assertFalse(IsArticleLikedUseCase(repo).invoke(1, 10))
    }

    @Test
    fun `GetLikesCountUseCase returns count`() = runTest {
        whenever(repo.getLikesCount(10)).thenReturn(42)
        assertEquals(42, GetLikesCountUseCase(repo).invoke(10))
    }

    @Test
    fun `SearchArticlesUseCase with empty query`() = runTest {
        whenever(repo.searchArticles("")).thenReturn(listOf())
        SearchArticlesUseCase(repo).invoke("")
        verify(repo).searchArticles("")
    }
}
